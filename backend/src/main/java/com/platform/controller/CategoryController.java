package com.platform.controller;

import com.platform.common.Result;
import com.platform.entity.PromptCategory;
import com.platform.service.PromptCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin
public class CategoryController {
    
    @Autowired
    private PromptCategoryService promptCategoryService;
    
    /**
     * 获取所有分类
     */
    @GetMapping("/list")
    public Result<List<PromptCategory>> getAllCategories() {
        List<PromptCategory> categories = promptCategoryService.list();
        return Result.success(categories);
    }
    
    /**
     * 获取分类详情
     */
    @GetMapping("/{id}")
    public Result<PromptCategory> getCategory(@PathVariable Long id) {
        PromptCategory category = promptCategoryService.getById(id);
        return category != null ? Result.success(category) : Result.error("分类不存在");
    }
}