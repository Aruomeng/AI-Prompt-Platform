package com.platform.controller;

import cn.hutool.core.util.StrUtil;
import com.platform.common.Result;
import com.platform.dto.UserLoginDTO;
import com.platform.dto.UserRegisterDTO;
import com.platform.entity.User;
import com.platform.service.UserService;
import com.platform.utils.JwtUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@CrossOrigin
@Tag(name = "认证管理", description = "用户注册、登录等认证相关接口")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    /**
     * 用户注册
     */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "新用户注册，需要提供用户名、密码和邮箱")
    public Result<User> register(@RequestBody UserRegisterDTO registerDTO) {
        try {
            log.info("User registration attempted: username={}", registerDTO.getUsername());
            
            User user = userService.register(
                registerDTO.getUsername(),
                registerDTO.getPassword(),
                registerDTO.getEmail()
            );
            
            // 不返回密码
            user.setPassword(null);
            
            log.info("User registered successfully: userId={}", user.getId());
            return Result.success(user);
        } catch (Exception e) {
            log.error("User registration failed: username={}, error={}", 
                registerDTO.getUsername(), e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 用户登录
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户登录，返回JWT Token和用户信息")
    public Result<Map<String, Object>> login(@RequestBody UserLoginDTO loginDTO) {
        try {
            log.info("User login attempted: username={}", loginDTO.getUsername());
            
            User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
            
            // 生成token
            String token = jwtUtils.generateToken(user.getId(), user.getUsername(), user.getRole());
            
            // 构建响应数据
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("user", user);
            
            // 不返回密码
            user.setPassword(null);
            
            log.info("User logged in successfully: userId={}", user.getId());
            return Result.success(data);
        } catch (Exception e) {
            log.error("User login failed: username={}, error={}", 
                loginDTO.getUsername(), e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/user-info")
    @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的信息")
    public Result<User> getUserInfo(@RequestAttribute("userId") Long userId) {
        try {
            User user = userService.getById(userId);
            if (user != null) {
                user.setPassword(null);
                log.debug("User info retrieved: userId={}", userId);
                return Result.success(user);
            }
            
            log.warn("User not found: userId={}", userId);
            return Result.error("用户不存在");
        } catch (Exception e) {
            log.error("Failed to get user info: userId={}, error={}", 
                userId, e.getMessage(), e);
            return Result.error(e.getMessage());
        }
    }
}