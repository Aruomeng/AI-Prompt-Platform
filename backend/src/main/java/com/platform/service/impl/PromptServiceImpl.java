package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.entity.Prompt;
import com.platform.entity.PromptCategory;
import com.platform.entity.User;
import com.platform.entity.UserFavorite;
import com.platform.entity.UserLike;
import com.platform.mapper.PromptMapper;
import com.platform.mapper.UserFavoriteMapper;
import com.platform.mapper.UserLikeMapper;
import com.platform.mapper.UserMapper;
import com.platform.mapper.PromptCategoryMapper;
import com.platform.service.PromptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提示词服务实现类
 */
@Service
public class PromptServiceImpl extends ServiceImpl<PromptMapper, Prompt> implements PromptService {
    
    @Autowired
    private UserLikeMapper userLikeMapper;
    
    @Autowired
    private UserFavoriteMapper userFavoriteMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private PromptCategoryMapper promptCategoryMapper;
    
    @Override
    public IPage<Prompt> getApprovedPromptsPage(Long page, Long size, Long categoryId, String keyword) {
        Page<Prompt> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Prompt::getStatus, "APPROVED")
               .orderByDesc(Prompt::getLikeCount)
               .orderByDesc(Prompt::getFavoriteCount)
               .orderByDesc(Prompt::getCreateTime);
        
        if (categoryId != null) {
            wrapper.eq(Prompt::getCategoryId, categoryId);
        }
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Prompt::getTitle, keyword)
                   .or()
                   .like(Prompt::getContent, keyword)
                   .or()
                   .like(Prompt::getTags, keyword);
        }
        
        return page(pageParam, wrapper);
    }
    
    @Override
    @Transactional
    public Prompt createPrompt(Prompt prompt) {
        prompt.setUsageCount(0);
        prompt.setLikeCount(0);
        prompt.setFavoriteCount(0);
        prompt.setStatus("PENDING");
        save(prompt);
        return prompt;
    }
    
    @Override
    @Transactional
    public boolean updatePrompt(Prompt prompt) {
        return updateById(prompt);
    }
    
    @Override
    @Transactional
    public boolean deletePrompt(Long promptId, Long userId) {
        Prompt prompt = getById(promptId);
        if (prompt == null || !prompt.getUserId().equals(userId)) {
            return false;
        }
        return removeById(promptId);
    }
    
    @Override
    @Transactional
    public boolean likePrompt(Long promptId, Long userId) {
        // 检查是否已经点赞
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getPromptId, promptId)
               .eq(UserLike::getUserId, userId);
        
        if (userLikeMapper.selectCount(wrapper) > 0) {
            return false;
        }
        
        // 添加点赞记录
        UserLike userLike = new UserLike();
        userLike.setPromptId(promptId);
        userLike.setUserId(userId);
        userLikeMapper.insert(userLike);
        
        // 更新点赞数
        Prompt prompt = getById(promptId);
        if (prompt != null) {
            prompt.setLikeCount(prompt.getLikeCount() + 1);
            updateById(prompt);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean unlikePrompt(Long promptId, Long userId) {
        // 删除点赞记录
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getPromptId, promptId)
               .eq(UserLike::getUserId, userId);
        
        int deleted = userLikeMapper.delete(wrapper);
        if (deleted > 0) {
            // 更新点赞数
            Prompt prompt = getById(promptId);
            if (prompt != null && prompt.getLikeCount() > 0) {
                prompt.setLikeCount(prompt.getLikeCount() - 1);
                updateById(prompt);
            }
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean favoritePrompt(Long promptId, Long userId) {
        // 检查是否已经收藏
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getPromptId, promptId)
               .eq(UserFavorite::getUserId, userId);
        
        if (userFavoriteMapper.selectCount(wrapper) > 0) {
            return false;
        }
        
        // 添加收藏记录
        UserFavorite userFavorite = new UserFavorite();
        userFavorite.setPromptId(promptId);
        userFavorite.setUserId(userId);
        userFavoriteMapper.insert(userFavorite);
        
        // 更新收藏数
        Prompt prompt = getById(promptId);
        if (prompt != null) {
            prompt.setFavoriteCount(prompt.getFavoriteCount() + 1);
            updateById(prompt);
        }
        
        return true;
    }
    
    @Override
    @Transactional
    public boolean unfavoritePrompt(Long promptId, Long userId) {
        // 删除收藏记录
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getPromptId, promptId)
               .eq(UserFavorite::getUserId, userId);
        
        int deleted = userFavoriteMapper.delete(wrapper);
        if (deleted > 0) {
            // 更新收藏数
            Prompt prompt = getById(promptId);
            if (prompt != null && prompt.getFavoriteCount() > 0) {
                prompt.setFavoriteCount(prompt.getFavoriteCount() - 1);
                updateById(prompt);
            }
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public boolean incrementUsageCount(Long promptId) {
        Prompt prompt = getById(promptId);
        if (prompt != null) {
            prompt.setUsageCount(prompt.getUsageCount() + 1);
            return updateById(prompt);
        }
        return false;
    }
    
    @Override
    public boolean hasLiked(Long promptId, Long userId) {
        LambdaQueryWrapper<UserLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserLike::getPromptId, promptId)
               .eq(UserLike::getUserId, userId);
        return userLikeMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public boolean hasFavorited(Long promptId, Long userId) {
        LambdaQueryWrapper<UserFavorite> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserFavorite::getPromptId, promptId)
               .eq(UserFavorite::getUserId, userId);
        return userFavoriteMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public IPage<Prompt> getPendingPrompts(Long page, Long size) {
        Page<Prompt> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        
        wrapper.eq(Prompt::getStatus, "PENDING")
               .orderByDesc(Prompt::getCreateTime);
        
        IPage<Prompt> pageResult = page(pageParam, wrapper);
        
        // 填充用户信息和分类名称
        if (pageResult.getRecords() != null) {
            for (Prompt prompt : pageResult.getRecords()) {
                // 填充用户信息
                User user = userMapper.selectById(prompt.getUserId());
                if (user != null) {
                    prompt.setUser(user);
                }
                
                // 填充分类名称
                PromptCategory category = promptCategoryMapper.selectById(prompt.getCategoryId());
                if (category != null) {
                    prompt.setCategoryName(category.getName());
                }
            }
        }
        
        return pageResult;
    }
    
    @Override
    public IPage<Prompt> getAllPrompts(Long page, Long size, String keyword, String status) {
        Page<Prompt> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Prompt::getTitle, keyword)
                   .or()
                   .like(Prompt::getContent, keyword)
                   .or()
                   .like(Prompt::getTags, keyword);
        }
        
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Prompt::getStatus, status);
        }
        
        wrapper.orderByDesc(Prompt::getCreateTime);
        
        IPage<Prompt> pageResult = page(pageParam, wrapper);
        
        // 填充用户信息和分类名称
        if (pageResult.getRecords() != null) {
            for (Prompt prompt : pageResult.getRecords()) {
                // 填充用户信息
                User user = userMapper.selectById(prompt.getUserId());
                if (user != null) {
                    prompt.setUser(user);
                }
                
                // 填充分类名称
                PromptCategory category = promptCategoryMapper.selectById(prompt.getCategoryId());
                if (category != null) {
                    prompt.setCategoryName(category.getName());
                }
            }
        }
        
        return pageResult;
    }
    
    @Override
    @Transactional
    public boolean auditPrompt(Long promptId, String status, String reviewComment, Long adminId) {
        Prompt prompt = getById(promptId);
        if (prompt == null) {
            return false;
        }
        
        prompt.setStatus(status);
        prompt.setReviewComment(reviewComment);
        prompt.setAuditUserId(adminId);
        prompt.setAuditTime(java.time.LocalDateTime.now());
        
        return updateById(prompt);
    }
    
    @Override
    @Transactional
    public boolean deletePromptByAdmin(Long promptId) {
        return removeById(promptId);
    }
    
    @Override
    public IPage<Prompt> getUserPromptsPage(Long userId, Long page, Long size) {
        Page<Prompt> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<Prompt> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Prompt::getUserId, userId)
               .orderByDesc(Prompt::getCreateTime);
        return page(pageParam, wrapper);
    }
    
    @Override
    public IPage<Prompt> getUserFavoritesPage(Long userId, Long page, Long size) {
        Page<Prompt> pageParam = new Page<>(page, size);
        
        // 使用联表查询获取用户收藏的提示词
        return baseMapper.selectUserFavoritesPage(pageParam, userId);
    }
}