package com.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI提示词共享平台启动类
 */
@SpringBootApplication
@MapperScan("com.platform.mapper")
public class AiPromptPlatformApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(AiPromptPlatformApplication.class, args);
    }
}