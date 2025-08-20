package com.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.common.Result;
import com.platform.entity.Comment;
import com.platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 评论控制器
 */
@RestController
@RequestMapping("/api/comments")
@CrossOrigin
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    /**
     * 获取提示词的评论列表
     */
    @GetMapping("/prompt/{promptId}")
    public Result<List<Comment>> getCommentsByPromptId(@PathVariable Long promptId) {
        System.out.println("[DEBUG] CommentController.getCommentsByPromptId called with promptId: " + promptId);
        try {
            List<Comment> comments = commentService.getCommentsByPromptId(promptId);
            System.out.println("[DEBUG] Successfully retrieved " + comments.size() + " comments for promptId: " + promptId);
            return Result.success(comments);
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to get comments for promptId: " + promptId);
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * 获取评论的用户信息
     */
    @GetMapping("/{id}/user")
    public Result<Long> getCommentUserId(@PathVariable Long id) {
        Long userId = commentService.getCommentUserId(id);
        return Result.success(userId);
    }
    
    /**
     * 添加评论
     */
    @PostMapping
    public Result<Comment> addComment(@RequestBody Comment comment, @RequestAttribute("userId") Long userId) {
        comment.setUserId(userId);
        Comment savedComment = commentService.addComment(comment);
        return Result.success(savedComment);
    }
    
    /**
     * 删除评论
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteComment(@PathVariable Long id, @RequestAttribute("userId") Long userId) {
        boolean success = commentService.deleteComment(id, userId);
        return success ? Result.success() : Result.error("删除评论失败");
    }
    
    /**
     * 获取评论的回复列表
     */
    @GetMapping("/{parentId}/replies")
    public Result<List<Comment>> getReplies(@PathVariable Long parentId) {
        List<Comment> replies = commentService.getReplies(parentId);
        return Result.success(replies);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/{id}/like")
    public Result<Void> likeComment(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        boolean success = commentService.likeComment(id, userId);
        return success ? Result.success() : Result.error("点赞失败");
    }
    
    /**
     * 取消点赞评论
     */
    @DeleteMapping("/{id}/like")
    public Result<Void> unlikeComment(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        boolean success = commentService.unlikeComment(id, userId);
        return success ? Result.success() : Result.error("取消点赞失败");
    }
    
    /**
     * 检查评论点赞状态
     */
    @GetMapping("/{id}/like-status")
    public Result<Boolean> checkCommentLikeStatus(
            @PathVariable Long id,
            @RequestAttribute("userId") Long userId) {
        boolean liked = commentService.hasLikedComment(id, userId);
        return Result.success(liked);
    }
    
    /**
     * 批量获取用户点赞的评论ID
     */
    @PostMapping("/liked-ids")
    public Result<List<Long>> getUserLikedCommentIds(
            @RequestBody List<Long> commentIds,
            @RequestAttribute("userId") Long userId) {
        List<Long> likedIds = commentService.getUserLikedCommentIds(userId, commentIds);
        return Result.success(likedIds);
    }
}