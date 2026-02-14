package com.platform.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import java.util.Locale;

/**
 * 国际化配置
 */
@Configuration
public class I18nConfig {
    
    @Bean
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages/messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        // 设置默认语言为中文
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        return messageSource;
    }
}
