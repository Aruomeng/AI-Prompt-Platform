package com.platform.service;

import com.platform.entity.User;
import com.platform.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UserService单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {
    
    @Mock
    private UserMapper userMapper;
    
    @InjectMocks
    private UserService userService;
    
    private User testUser;
    private BCryptPasswordEncoder passwordEncoder;
    
    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setNickname("Test User");
        testUser.setRole("USER");
        testUser.setCreateTime(LocalDateTime.now());
        testUser.setUpdateTime(LocalDateTime.now());
        
        // 注入passwordEncoder到service中
        ReflectionTestUtils.setField(userService, "passwordEncoder", passwordEncoder);
    }
    
    @Test
    @DisplayName("用户注册 - 成功")
    void testRegisterSuccess() {
        // 准备
        when(userMapper.selectByUsername("newuser"))
            .thenReturn(null);
        when(userMapper.selectByEmail("new@example.com"))
            .thenReturn(null);
        when(userMapper.insert(any(User.class)))
            .thenReturn(1);
        
        // 执行
        User result = userService.register("newuser", "password123", "new@example.com");
        
        // 验证
        assertNotNull(result);
        verify(userMapper, times(1)).insert(any(User.class));
    }
    
    @Test
    @DisplayName("用户注册 - 用户名已存在")
    void testRegisterUsernameExists() {
        // 准备
        when(userMapper.selectByUsername("testuser"))
            .thenReturn(testUser);
        
        // 执行和验证
        assertThrows(RuntimeException.class, () -> {
            userService.register("testuser", "password123", "new@example.com");
        });
    }
    
    @Test
    @DisplayName("用户注册 - 邮箱已存在")
    void testRegisterEmailExists() {
        // 准备
        when(userMapper.selectByUsername("newuser"))
            .thenReturn(null);
        when(userMapper.selectByEmail("test@example.com"))
            .thenReturn(testUser);
        
        // 执行和验证
        assertThrows(RuntimeException.class, () -> {
            userService.register("newuser", "password123", "test@example.com");
        });
    }
    
    @Test
    @DisplayName("用户登录 - 成功")
    void testLoginSuccess() {
        // 准备
        when(userMapper.selectByUsername("testuser"))
            .thenReturn(testUser);
        
        // 执行
        User result = userService.login("testuser", "password123");
        
        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
    
    @Test
    @DisplayName("用户登录 - 用户不存在")
    void testLoginUserNotFound() {
        // 准备
        when(userMapper.selectByUsername("nonexistent"))
            .thenReturn(null);
        
        // 执行和验证
        assertThrows(RuntimeException.class, () -> {
            userService.login("nonexistent", "password123");
        });
    }
    
    @Test
    @DisplayName("用户登录 - 密码错误")
    void testLoginPasswordIncorrect() {
        // 准备
        when(userMapper.selectByUsername("testuser"))
            .thenReturn(testUser);
        
        // 执行和验证
        assertThrows(RuntimeException.class, () -> {
            userService.login("testuser", "wrongpassword");
        });
    }
    
    @Test
    @DisplayName("按ID获取用户")
    void testGetUserById() {
        // 准备
        when(userMapper.selectById(1L))
            .thenReturn(testUser);
        
        // 执行
        User result = userService.getById(1L);
        
        // 验证
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
    }
    
    @Test
    @DisplayName("更新用户信息")
    void testUpdateUser() {
        // 准备
        testUser.setNickname("Updated User");
        when(userMapper.updateById(any(User.class)))
            .thenReturn(1);
        
        // 执行
        boolean result = userService.updateById(testUser);
        
        // 验证
        assertTrue(result);
        verify(userMapper, times(1)).updateById(any(User.class));
    }
    
    @Test
    @DisplayName("删除用户 - 逻辑删除")
    void testDeleteUser() {
        // 准备
        testUser.setDeleted(1);
        when(userMapper.updateById(any(User.class)))
            .thenReturn(1);
        
        // 执行
        boolean result = userService.removeById(1L);
        
        // 验证
        assertTrue(result);
    }
}
