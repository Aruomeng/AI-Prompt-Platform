package com.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.entity.Prompt;

/**
 * 提示词服务接口
 */
public interface PromptService extends IService<Prompt> {
    
    /**
     * 分页获取已审核的提示词
     */
    IPage<Prompt> getApprovedPromptsPage(Long page, Long size, Long categoryId, String keyword);
    
    /**
     * 创建提示词
     */
    Prompt createPrompt(Prompt prompt);
    
    /**
     * 更新提示词
     */
    boolean updatePrompt(Prompt prompt);
    
    /**
     * 删除提示词
     */
    boolean deletePrompt(Long promptId, Long userId);
    
    /**
     * 点赞提示词
     */
    boolean likePrompt(Long promptId, Long userId);
    
    /**
     * 取消点赞提示词
     */
    boolean unlikePrompt(Long promptId, Long userId);
    
    /**
     * 收藏提示词
     */
    boolean favoritePrompt(Long promptId, Long userId);
    
    /**
     * 取消收藏提示词
     */
    boolean unfavoritePrompt(Long promptId, Long userId);
    
    /**
     * 增加使用次数
     */
    boolean incrementUsageCount(Long promptId);
    
    /**
     * 判断用户是否点赞
     */
    boolean hasLiked(Long promptId, Long userId);
    
    /**
     * 判断用户是否收藏
     */
    boolean hasFavorited(Long promptId, Long userId);
    
    /**
     * 获取用户的提示词
     */
    IPage<Prompt> getUserPromptsPage(Long userId, Long page, Long size);
    
    /**
     * 获取用户收藏的提示词
     */
    IPage<Prompt> getUserFavoritesPage(Long userId, Long page, Long size);
    
    /**
     * 获取待审核的提示词
     */
    IPage<Prompt> getPendingPrompts(Long page, Long size);
    
    /**
     * 获取所有提示词（管理用）
     */
    IPage<Prompt> getAllPrompts(Long page, Long size, String keyword, String status);
    
    /**
     * 审核提示词
     */
    boolean auditPrompt(Long promptId, String status, String reviewComment, Long adminId);
    
    /**
     * 管理员删除提示词
     */
    boolean deletePromptByAdmin(Long promptId);
}