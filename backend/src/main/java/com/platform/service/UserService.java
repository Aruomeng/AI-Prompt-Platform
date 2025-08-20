package com.platform.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.entity.User;

/**
 * 用户服务接口
 */
public interface UserService extends IService<User> {
    
    /**
     * 用户注册
     */
    User register(String username, String password, String email);
    
    /**
     * 用户登录
     */
 User login(String username, String password);
    
    /**
     * 根据用户名查找用户
     */
    User getByUsername(String username);
}