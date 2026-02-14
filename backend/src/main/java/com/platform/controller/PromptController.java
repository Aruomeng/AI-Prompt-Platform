package com.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.common.PageResult;
import com.platform.common.Result;
import com.platform.dto.PromptCreateDTO;
import com.platform.entity.Prompt;
import com.platform.service.PromptService;
import com.platform.utils.CacheUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 提示词控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/prompts")
@CrossOrigin
@Tag(name = "提示词管理", description = "提示词的增删改查、点赞、收藏等操作")
public class PromptController {
    
    @Autowired
    private PromptService promptService;
    
    @Autowired(required = false)
    private CacheUtils cacheUtils;
    
    private static final String PROMPT_LIST_CACHE_KEY = "prompt:list:%s:%s:%s";
    private static final String PROMPT_DETAIL_CACHE_KEY = "prompt:detail:%s";
    
    /**
     * 获取提示词列表（分页）
     */
    @GetMapping("/public/list")
    @Operation(summary = "获取提示词列表", description = "分页获取已审核的提示词列表，支持分类和关键词筛选")
    public Result<PageResult<Prompt>> getPromptList(
            @Parameter(description = "页码，默认1") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页数量，默认10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "分类ID") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "关键词") @RequestParam(required = false) String keyword) {
        
        try {
            // 尝试从缓存获取
            String cacheKey = String.format(PROMPT_LIST_CACHE_KEY, page, size, categoryId != null ? categoryId : "all");
            if (cacheUtils != null) {
                Object cachedData = cacheUtils.get(cacheKey);
                if (cachedData != null) {
                    log.debug("Hit cache for prompt list: page={}, size={}, categoryId={}", page, size, categoryId);
                    return Result.success((PageResult<Prompt>) cachedData);
                }
            }
            
            IPage<Prompt> pageResult = promptService.getApprovedPromptsPage(page, size, categoryId, keyword);
            PageResult<Prompt> result = new PageResult<>();
            result.setTotal(pageResult.getTotal());
            result.setRecords(pageResult.getRecords());
            result.setCurrent(pageResult.getCurrent());
            result.setSize(pageResult.getSize());
            result.setPages(pageResult.getPages());
            
            // 缓存结果
            if (cacheUtils != null) {
                cacheUtils.set(cacheKey, result, 300); // 5分钟缓存
            }
            
            log.debug("Fetched prompt list: page={}, size={}, total={}", page, size, result.getTotal());
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get prompt list: page={}, size={}, error={}", page, size, e.getMessage(), e);
            return Result.error("获取提示词列表失败");
        }
    }
    
    /**
     * 获取提示词详情
     */
    @GetMapping("/public/{id}")
    @Operation(summary = "获取提示词详情", description = "根据ID获取提示词详细信息")
    public Result<Prompt> getPromptDetail(
            @Parameter(description = "提示词ID") @PathVariable Long id) {
        
        try {
            // 尝试从缓存获取
            String cacheKey = String.format(PROMPT_DETAIL_CACHE_KEY, id);
            if (cacheUtils != null) {
                Object cachedData = cacheUtils.get(cacheKey);
                if (cachedData != null) {
                    log.debug("Hit cache for prompt detail: id={}", id);
                    return Result.success((Prompt) cachedData);
                }
            }
            
            Prompt prompt = promptService.getById(id);
            if (prompt != null && "APPROVED".equals(prompt.getStatus())) {
                // 增加使用次数
                promptService.incrementUsageCount(id);
                
                // 缓存结果
                if (cacheUtils != null) {
                    cacheUtils.set(cacheKey, prompt, 600); // 10分钟缓存
                }
                
                log.debug("Fetched prompt detail: id={}", id);
                return Result.success(prompt);
            }
            
            log.warn("Prompt not found or not approved: id={}", id);
            return Result.error("提示词不存在或未审核");
        } catch (Exception e) {
            log.error("Failed to get prompt detail: id={}, error={}", id, e.getMessage(), e);
            return Result.error("获取提示词详情失败");
        }
    }
    
    /**
     * 创建提示词
     */
    @PostMapping("/create")
    @Operation(summary = "创建提示词", description = "创建新的提示词，创建后需要管理员审核")
    public Result<Prompt> createPrompt(
            @RequestBody PromptCreateDTO createDTO,
            @RequestAttribute("userId") Long userId) {
        
        try {
            Prompt prompt = new Prompt();
            prompt.setTitle(createDTO.getTitle());
            prompt.setContent(createDTO.getContent());
            prompt.setDescription(createDTO.getDescription());
            prompt.setCategoryId(createDTO.getCategoryId());
            prompt.setTags(createDTO.getTags());
            prompt.setUserId(userId);
            
            Prompt savedPrompt = promptService.createPrompt(prompt);
            
            // 清除列表缓存
            if (cacheUtils != null) {
                cacheUtils.delete("prompt:list:*:*:*");
            }
            
            log.info("Prompt created: id={}, userId={}", savedPrompt.getId(), userId);
            return Result.success(savedPrompt);
        } catch (Exception e) {
            log.error("Failed to create prompt: userId={}, error={}", userId, e.getMessage(), e);
            return Result.error("创建提示词失败");
        }
    }

    /**
     * 更新提示词
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新提示词", description = "更新已创建的提示词，只有提示词拥有者可以更新")
    public Result<Prompt> updatePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestBody PromptCreateDTO updateDTO,
            @RequestAttribute("userId") Long userId) {
        
        try {
            Prompt existingPrompt = promptService.getById(id);
            if (existingPrompt == null || !existingPrompt.getUserId().equals(userId)) {
                log.warn("Unauthorized update attempt: promptId={}, userId={}", id, userId);
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
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                    cacheUtils.delete("prompt:list:*:*:*");
                }
                
                log.info("Prompt updated: id={}, userId={}", id, userId);
                return Result.success(existingPrompt);
            } else {
                log.error("Failed to update prompt: id={}", id);
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            log.error("Failed to update prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("更新提示词失败");
        }
    }
    
    /**
     * 获取用户的提示词
     */
    @GetMapping("/my")
    @Operation(summary = "获取我的提示词", description = "获取当前登录用户创建的提示词列表")
    public Result<PageResult<Prompt>> getMyPrompts(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @RequestAttribute("userId") Long userId) {
        
        try {
            IPage<Prompt> pageResult = promptService.getUserPromptsPage(userId, page, size);
            PageResult<Prompt> result = new PageResult<>();
            result.setTotal(pageResult.getTotal());
            result.setRecords(pageResult.getRecords());
            result.setCurrent(pageResult.getCurrent());
            result.setSize(pageResult.getSize());
            result.setPages(pageResult.getPages());
            
            log.debug("Fetched user prompts: userId={}, page={}, size={}", userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get user prompts: userId={}, error={}", userId, e.getMessage(), e);
            return Result.error("获取我的提示词失败");
        }
    }
    
    /**
     * 获取用户收藏的提示词
     */
    @GetMapping("/my-favorites")
    @Operation(summary = "获取我的收藏", description = "获取当前登录用户收藏的提示词列表")
    public Result<PageResult<Prompt>> getMyFavorites(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Long page,
            @Parameter(description = "每页数量") @RequestParam(defaultValue = "10") Long size,
            @RequestAttribute("userId") Long userId) {
        
        try {
            IPage<Prompt> pageResult = promptService.getUserFavoritesPage(userId, page, size);
            PageResult<Prompt> result = new PageResult<>();
            result.setTotal(pageResult.getTotal());
            result.setRecords(pageResult.getRecords());
            result.setCurrent(pageResult.getCurrent());
            result.setSize(pageResult.getSize());
            result.setPages(pageResult.getPages());
            
            log.debug("Fetched user favorites: userId={}, page={}, size={}", userId, page, size);
            return Result.success(result);
        } catch (Exception e) {
            log.error("Failed to get user favorites: userId={}, error={}", userId, e.getMessage(), e);
            return Result.error("获取收藏列表失败");
        }
    }
    
    /**
     * 删除提示词
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除提示词", description = "删除指定的提示词，只有拥有者可以删除")
    public Result<Void> deletePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean success = promptService.deletePrompt(id, userId);
            if (success) {
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                    cacheUtils.delete("prompt:list:*:*:*");
                }
                
                log.info("Prompt deleted: id={}, userId={}", id, userId);
                return Result.success();
            } else {
                log.warn("Failed to delete prompt: id={}, userId={}", id, userId);
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            log.error("Failed to delete prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("删除提示词失败");
        }
    }
    
    /**
     * 点赞提示词
     */
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞提示词", description = "对指定的提示词进行点赞")
    public Result<Void> likePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean success = promptService.likePrompt(id, userId);
            if (success) {
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                }
                
                log.info("Prompt liked: id={}, userId={}", id, userId);
                return Result.success();
            } else {
                log.warn("Failed to like prompt: id={}, userId={}", id, userId);
                return Result.error("点赞失败");
            }
        } catch (Exception e) {
            log.error("Failed to like prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("点赞失败");
        }
    }
    
    /**
     * 取消点赞提示词
     */
    @DeleteMapping("/{id}/like")
    @Operation(summary = "取消点赞", description = "取消对指定提示词的点赞")
    public Result<Void> unlikePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean success = promptService.unlikePrompt(id, userId);
            if (success) {
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                }
                
                log.info("Prompt unliked: id={}, userId={}", id, userId);
                return Result.success();
            } else {
                log.warn("Failed to unlike prompt: id={}, userId={}", id, userId);
                return Result.error("取消点赞失败");
            }
        } catch (Exception e) {
            log.error("Failed to unlike prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("取消点赞失败");
        }
    }
    
    /**
     * 收藏提示词
     */
    @PostMapping("/{id}/favorite")
    @Operation(summary = "收藏提示词", description = "收藏指定的提示词")
    public Result<Void> favoritePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean success = promptService.favoritePrompt(id, userId);
            if (success) {
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                }
                
                log.info("Prompt favorited: id={}, userId={}", id, userId);
                return Result.success();
            } else {
                log.warn("Failed to favorite prompt: id={}, userId={}", id, userId);
                return Result.error("收藏失败");
            }
        } catch (Exception e) {
            log.error("Failed to favorite prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("收藏失败");
        }
    }
    
    /**
     * 取消收藏提示词
     */
    @DeleteMapping("/{id}/favorite")
    @Operation(summary = "取消收藏", description = "取消对指定提示词的收藏")
    public Result<Void> unfavoritePrompt(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean success = promptService.unfavoritePrompt(id, userId);
            if (success) {
                // 清除缓存
                if (cacheUtils != null) {
                    cacheUtils.delete(String.format(PROMPT_DETAIL_CACHE_KEY, id));
                }
                
                log.info("Prompt unfavorited: id={}, userId={}", id, userId);
                return Result.success();
            } else {
                log.warn("Failed to unfavorite prompt: id={}, userId={}", id, userId);
                return Result.error("取消收藏失败");
            }
        } catch (Exception e) {
            log.error("Failed to unfavorite prompt: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("取消收藏失败");
        }
    }
    
    /**
     * 检查点赞状态
     */
    @GetMapping("/{id}/like-status")
    @Operation(summary = "检查点赞状态", description = "检查当前用户是否对指定提示词进行了点赞")
    public Result<Boolean> checkLikeStatus(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean liked = promptService.hasLiked(id, userId);
            log.debug("Checked like status: id={}, userId={}, liked={}", id, userId, liked);
            return Result.success(liked);
        } catch (Exception e) {
            log.error("Failed to check like status: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("检查点赞状态失败");
        }
    }
    
    /**
     * 检查收藏状态
     */
    @GetMapping("/{id}/favorite-status")
    @Operation(summary = "检查收藏状态", description = "检查当前用户是否收藏了指定提示词")
    public Result<Boolean> checkFavoriteStatus(
            @Parameter(description = "提示词ID") @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        
        try {
            boolean favorited = promptService.hasFavorited(id, userId);
            log.debug("Checked favorite status: id={}, userId={}, favorited={}", id, userId, favorited);
            return Result.success(favorited);
        } catch (Exception e) {
            log.error("Failed to check favorite status: id={}, userId={}, error={}", id, userId, e.getMessage(), e);
            return Result.error("检查收藏状态失败");
        }
    }
}