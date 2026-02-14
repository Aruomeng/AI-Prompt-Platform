package com.platform.exception;

/**
 * 业务异常
 */
public class BusinessException extends RuntimeException {
    
    private Integer code;
    
    private String messageKey;
    
    public BusinessException(String message) {
        super(message);
        this.code = 500;
    }
    
    public BusinessException(Integer code, String message) {
        super(message);
        this.code = code;
    }
    
    public BusinessException(String messageKey, String message) {
        super(message);
        this.messageKey = messageKey;
        this.code = 500;
    }
    
    public BusinessException(Integer code, String messageKey, String message) {
        super(message);
        this.code = code;
        this.messageKey = messageKey;
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessageKey() {
        return messageKey;
    }
    
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
