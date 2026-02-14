package com.platform.exception;

import com.platform.common.Result;
import com.platform.utils.I18nUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @Autowired(required = false)
    private I18nUtils i18nUtils;
    
    /**
     * 业务异常处理
     */
    @ExceptionHandler(BusinessException.class)
    public Result<?> handleBusinessException(BusinessException ex, WebRequest request) {
        log.warn("Business exception occurred: {}", ex.getMessage(), ex);
        
        String message = ex.getMessage();
        if (ex.getMessageKey() != null && i18nUtils != null) {
            message = i18nUtils.getMessage(ex.getMessageKey());
        }
        
        return Result.error(ex.getCode(), message);
    }
    
    /**
     * 参数验证异常处理
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<?> handleValidationException(MethodArgumentNotValidException ex, WebRequest request) {
        log.warn("Validation exception occurred: {}", ex.getMessage());
        
        String message = ex.getBindingResult().getAllErrors().stream()
            .map(error -> error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        return Result.error(HttpStatus.BAD_REQUEST.value(), message);
    }
    
    /**
     * 通用异常处理
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception ex, WebRequest request) {
        log.error("Unexpected exception occurred: ", ex);
        
        return Result.error(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "系统内部错误，请稍后重试"
        );
    }
}
