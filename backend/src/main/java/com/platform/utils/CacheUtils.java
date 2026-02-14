package com.platform.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Redis缓存工具类
 */
@Slf4j
@Component
public class CacheUtils {
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 设置缓存
     *
     * @param key 缓存键
     * @param value 缓存值
     * @param timeout 超时时间（秒）
     */
    public void set(String key, Object value, long timeout) {
        try {
            redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            log.debug("Cache set: key={}, timeout={}s", key, timeout);
        } catch (Exception e) {
            log.error("Failed to set cache: key={}", key, e);
        }
    }
    
    /**
     * 设置缓存（默认24小时超时）
     *
     * @param key 缓存键
     * @param value 缓存值
     */
    public void set(String key, Object value) {
        set(key, value, 86400); // 24小时
    }
    
    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public Object get(String key) {
        try {
            return redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            log.error("Failed to get cache: key={}", key, e);
            return null;
        }
    }
    
    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public void delete(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Cache deleted: key={}", key);
        } catch (Exception e) {
            log.error("Failed to delete cache: key={}", key, e);
        }
    }
    
    /**
     * 删除多个缓存
     *
     * @param keys 缓存键列表
     */
    public void delete(String... keys) {
        try {
            for (String key : keys) {
                redisTemplate.delete(key);
            }
            log.debug("Caches deleted: keys={}", (Object[]) keys);
        } catch (Exception e) {
            log.error("Failed to delete caches", e);
        }
    }
    
    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 是否存在
     */
    public boolean exists(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check cache existence: key={}", key, e);
            return false;
        }
    }
    
    /**
     * 获取缓存过期时间（秒）
     *
     * @param key 缓存键
     * @return 过期时间（秒），-1表示永不过期，-2表示不存在
     */
    public long getExpire(String key) {
        try {
            Long expire = redisTemplate.getExpire(key, TimeUnit.SECONDS);
            return expire != null ? expire : -2;
        } catch (Exception e) {
            log.error("Failed to get cache expire: key={}", key, e);
            return -2;
        }
    }
    
    /**
     * 设置缓存过期时间
     *
     * @param key 缓存键
     * @param timeout 超时时间（秒）
     */
    public void expire(String key, long timeout) {
        try {
            redisTemplate.expire(key, timeout, TimeUnit.SECONDS);
            log.debug("Cache expire set: key={}, timeout={}s", key, timeout);
        } catch (Exception e) {
            log.error("Failed to set cache expire: key={}", key, e);
        }
    }
}
