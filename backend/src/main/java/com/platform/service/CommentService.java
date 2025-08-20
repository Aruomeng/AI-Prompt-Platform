package com.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.platform.entity.Comment;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import java.util.List;

/**
 * 评论服务接口
 */
public interface CommentService extends IService<Comment> {
    
    /**
     * 获取提示词的评论列表（包含用户信息）
     */
    List<Comment> getCommentsByPromptId(Long promptId);
    
    /**
     * 添加评论
     */
    Comment addComment(Comment comment);
    
    /**
     * 删除评论
     */
    boolean deleteComment(Long commentId, Long userId);
    
    /**
     * 获取评论的回复列表
     */
    List<Comment> getReplies(Long parentId);

    /**
     * 获取评论的用户ID
     */
    Long getCommentUserId(Long commentId);
    
    /**
     * 获取评论数量
     */
    int getCommentCount(Long promptId);
    
    /**
     * 点赞评论
     */
    boolean likeComment(Long commentId, Long userId);
    
    /**
     * 取消点赞评论
     */
    boolean unlikeComment(Long commentId, Long userId);
    
    /**
     * 检查用户是否点赞评论
     */
    boolean hasLikedComment(Long commentId, Long userId);
    
    /**
     * 获取用户点赞的评论ID列表
     */
    List<Long> getUserLikedCommentIds(Long userId, List<Long> commentIds);
    
    /**
     * 获取所有评论（管理用）
     */
    IPage<Comment> getAllComments(Long page, Long size, String keyword, String status);
    
    /**
     * 管理员删除评论
     */
    boolean deleteCommentByAdmin(Long commentId);
    
    /**
     * 批量删除评论
     */
    boolean batchDeleteComments(List<Long> commentIds);
}