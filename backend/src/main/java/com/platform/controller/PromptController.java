package com.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.common.PageResult;
import com.platform.common.Result;
import com.platform.dto.PromptCreateDTO;
import com.platform.entity.Prompt;
import com.platform.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 提示词控制器
 */
@RestController
@RequestMapping("/api/prompts")
@CrossOrigin
public class PromptController {
    
    @Autowired
    private PromptService promptService;
    
    /**
     * 获取提示词列表（分页）
     */
    @GetMapping("/public/list")
    public Result<PageResult<Prompt>> getPromptList(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyword) {
        
        IPage<Prompt> pageResult = promptService.getApprovedPromptsPage(page, size, categoryId, keyword);
        PageResult<Prompt> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }
    
    /**
     * 获取提示词详情
     */
    @GetMapping("/public/{id}")
    public Result<Prompt> getPromptDetail(@PathVariable Long id) {
        Prompt prompt = promptService.getById(id);
        if (prompt != null && "APPROVED".equals(prompt.getStatus())) {
            // 增加使用次数
            promptService.incrementUsageCount(id);
            return Result.success(prompt);
        }
        return Result.error("提示词不存在或未审核");
    }
    
    /**
     * 创建提示词
     */
    @PostMapping("/create")
    public Result<Prompt> createPrompt(
            @RequestBody PromptCreateDTO createDTO,
            @RequestAttribute("userId") Long userId) {
        
        Prompt prompt = new Prompt();
        prompt.setTitle(createDTO.getTitle());
        prompt.setContent(createDTO.getContent());
        prompt.setDescription(createDTO.getDescription());
        prompt.setCategoryId(createDTO.getCategoryId());
        prompt.setTags(createDTO.getTags());
        prompt.setUserId(userId);
        
        Prompt savedPrompt = promptService.createPrompt(prompt);
        return Result.success(savedPrompt);
    }

    /**
     * 更新提示词
     */
    @PutMapping("/{id}")
    public Result<Prompt> updatePrompt(
            @PathVariable Long id,
            @RequestBody PromptCreateDTO updateDTO,
            @RequestAttribute("userId") Long userId) {
        
        Prompt existingPrompt = promptService.getById(id);
        if (existingPrompt == null || !existingPrompt.getUserId().equals(userId)) {
            return Result.error("提示词不存在或无权限修改");
        }
        
        existingPrompt.setTitle(updateDTO.getTitle());
        existingPrompt.setContent(updateDTO.getContent());
        existingPrompt.setDescription(updateDTO.getDescription());
        existingPrompt.setCategoryId(updateDTO.getCategoryId());
        existingPrompt.setTags(updateDTO.getTags());
        existingPrompt.setStatus("PENDING"); // 修改后重新设置为待审核状态
        
        boolean success = promptService.updatePrompt(existingPrompt);
        if (success) {
            return Result.success(existingPrompt);
        } else {
            return Result.error("更新失败");
        }
    }
    
    /**
     * 获取用户的提示词
     */
    @GetMapping("/my")
    public Result<PageResult<Prompt>> getMyPrompts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestAttribute("userId") Long userId) {
        
        IPage<Prompt> pageResult = promptService.getUserPromptsPage(userId, page, size);
        PageResult<Prompt> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }
    
    /**
     * 获取用户收藏的提示词
     */
    @GetMapping("/my-favorites")
    public Result<PageResult<Prompt>> getMyFavorites(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestAttribute("userId") Long userId) {
        
        IPage<Prompt> pageResult = promptService.getUserFavoritesPage(userId, page, size);
        PageResult<Prompt> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }
    
    /**
     * 删除提示词
     */
    @DeleteMapping("/{id}")
    public Result<Void> deletePrompt(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = promptService.deletePrompt(id, userId);
        return success ? Result.success() : Result.error("删除失败");
    }
    
    /**
     * 点赞提示词
     */
    @PostMapping("/{id}/like")
    public Result<Void> likePrompt(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = promptService.likePrompt(id, userId);
        return success ? Result.success() : Result.error("点赞失败");
    }
    
    /**
     * 取消点赞提示词
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikePrompt(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = promptService.unlikePrompt(id, userId);
        return success ? Result.success() : Result.error("取消点赞失败");
    }
    
    /**
     * 收藏提示词
     */
    @PostMapping("/{id}/favorite")
    public Result<Void> favoritePrompt(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = promptService.favoritePrompt(id, userId);
        return success ? Result.success() : Result.error("收藏失败");
    }
    
    /**
     * 取消收藏提示词
     */
    @DeleteMapping("/{id}/favorite")
    public Result<Void> unfavoritePrompt(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean success = promptService.unfavoritePrompt(id, userId);
        return success ? Result.success() : Result.error("取消收藏失败");
    }
    
    /**
     * 检查点赞状态
     */
    @GetMapping("/{id}/like-status")
    public Result<Boolean> checkLikeStatus(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean liked = promptService.hasLiked(id, userId);
        return Result.success(liked);
    }
    
    /**
     * 检查收藏状态
     */
    @GetMapping("/{id}/favorite-status")
    public Result<Boolean> checkFavoriteStatus(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        boolean favorited = promptService.hasFavorited(id, userId);
        return Result.success(favorited);
    }
}