package com.platform.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * 国际化消息工具类
 */
@Component
public class I18nUtils {
    
    @Autowired
    private MessageSource messageSource;
    
    /**
     * 获取国际化消息
     *
     * @param code 消息代码
     * @return 本地化消息
     */
    public String getMessage(String code) {
        try {
            return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
    
    /**
     * 获取国际化消息（带参数）
     *
     * @param code 消息代码
     * @param args 参数
     * @return 本地化消息
     */
    public String getMessage(String code, Object[] args) {
        try {
            return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
        } catch (Exception e) {
            return code;
        }
    }
    
    /**
     * 获取指定语言的消息
     *
     * @param code 消息代码
     * @param locale 语言地区
     * @return 本地化消息
     */
    public String getMessage(String code, Locale locale) {
        try {
            return messageSource.getMessage(code, null, locale);
        } catch (Exception e) {
            return code;
        }
    }
    
    /**
     * 获取指定语言的消息（带参数）
     *
     * @param code 消息代码
     * @param args 参数
     * @param locale 语言地区
     * @return 本地化消息
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            return code;
        }
    }
}
