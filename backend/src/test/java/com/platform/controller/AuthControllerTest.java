package com.platform.controller;

import com.platform.common.Result;
import com.platform.dto.UserLoginDTO;
import com.platform.dto.UserRegisterDTO;
import com.platform.entity.User;
import com.platform.service.UserService;
import com.platform.utils.JwtUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthController单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("认证控制器测试")
class AuthControllerTest {
    
    @Mock
    private UserService userService;
    
    @Mock
    private JwtUtils jwtUtils;
    
    @InjectMocks
    private AuthController authController;
    
    private User testUser;
    private UserRegisterDTO registerDTO;
    private UserLoginDTO loginDTO;
    
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setNickname("Test User");
        testUser.setRole("USER");
        testUser.setPassword("encrypted_password");
        
        registerDTO = new UserRegisterDTO();
        registerDTO.setUsername("newuser");
        registerDTO.setPassword("password123");
        registerDTO.setEmail("newuser@example.com");
        
        loginDTO = new UserLoginDTO();
        loginDTO.setUsername("testuser");
        loginDTO.setPassword("password123");
    }
    
    @Test
    @DisplayName("用户注册成功")
    void testRegisterSuccess() {
        // 准备
        when(userService.register(anyString(), anyString(), anyString()))
            .thenReturn(testUser);
        
        // 执行
        Result<User> result = authController.register(registerDTO);
        
        // 验证
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNull(result.getData().getPassword());
        verify(userService, times(1)).register(anyString(), anyString(), anyString());
    }
    
    @Test
    @DisplayName("用户注册失败")
    void testRegisterFailed() {
        // 准备
        when(userService.register(anyString(), anyString(), anyString()))
            .thenThrow(new RuntimeException("用户名已存在"));
        
        // 执行
        Result<User> result = authController.register(registerDTO);
        
        // 验证
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("用户名已存在"));
    }
    
    @Test
    @DisplayName("用户登录成功")
    void testLoginSuccess() {
        // 准备
        when(userService.login(anyString(), anyString()))
            .thenReturn(testUser);
        when(jwtUtils.generateToken(anyLong(), anyString(), anyString()))
            .thenReturn("test-jwt-token");
        
        // 执行
        Result<Map<String, Object>> result = authController.login(loginDTO);
        
        // 验证
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertTrue(result.getData().containsKey("token"));
        assertTrue(result.getData().containsKey("user"));
        assertEquals("test-jwt-token", result.getData().get("token"));
        
        User returnedUser = (User) result.getData().get("user");
        assertNull(returnedUser.getPassword());
    }
    
    @Test
    @DisplayName("用户登录失败 - 用户不存在")
    void testLoginFailedUserNotFound() {
        // 准备
        when(userService.login(anyString(), anyString()))
            .thenThrow(new RuntimeException("用户不存在"));
        
        // 执行
        Result<Map<String, Object>> result = authController.login(loginDTO);
        
        // 验证
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("用户不存在"));
    }
    
    @Test
    @DisplayName("获取用户信息成功")
    void testGetUserInfoSuccess() {
        // 准备
        when(userService.getById(1L))
            .thenReturn(testUser);
        
        // 执行
        Result<User> result = authController.getUserInfo(1L);
        
        // 验证
        assertNotNull(result);
        assertEquals(200, result.getCode());
        assertNotNull(result.getData());
        assertEquals("testuser", result.getData().getUsername());
        assertNull(result.getData().getPassword());
    }
    
    @Test
    @DisplayName("获取用户信息失败 - 用户不存在")
    void testGetUserInfoNotFound() {
        // 准备
        when(userService.getById(999L))
            .thenReturn(null);
        
        // 执行
        Result<User> result = authController.getUserInfo(999L);
        
        // 验证
        assertNotNull(result);
        assertEquals(500, result.getCode());
        assertTrue(result.getMessage().contains("用户不存在"));
    }
}
