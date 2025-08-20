package com.platform.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 提示词更新DTO
 */
@Data
public class PromptUpdateDTO {
    
    @NotBlank(message = "标题不能为空")
    private String title;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private String description;
    
    @NotNull(message = "分类不能为空")
    private Long categoryId;
    
    private String tags;
}