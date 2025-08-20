package com.platform.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.entity.Comment;
import com.platform.entity.CommentLike;
import com.platform.entity.Prompt;
import com.platform.entity.PromptCategory;
import com.platform.entity.User;
import com.platform.mapper.CommentLikeMapper;
import com.platform.mapper.CommentMapper;
import com.platform.mapper.PromptCategoryMapper;
import com.platform.mapper.PromptMapper;
import com.platform.mapper.UserMapper;
import com.platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 评论服务实现类
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private CommentLikeMapper commentLikeMapper;
    
    @Autowired
    private PromptMapper promptMapper;
    
    @Autowired
    private PromptCategoryMapper promptCategoryMapper;
    
    @Override
    public List<Comment> getCommentsByPromptId(Long promptId) {
        System.out.println("[DEBUG] CommentServiceImpl.getCommentsByPromptId called with promptId: " + promptId);
        
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPromptId, promptId)
               .orderByDesc(Comment::getCreateTime);
        
        System.out.println("[DEBUG] Query wrapper: " + wrapper.getTargetSql());
        
        List<Comment> comments = list(wrapper);
        
        System.out.println("[DEBUG] Found " + comments.size() + " comments from database");
        
        // 填充用户信息和回复数量
        for (Comment comment : comments) {
            System.out.println("[DEBUG] Processing comment ID: " + comment.getId() + ", userId: " + comment.getUserId());
            User user = userMapper.selectById(comment.getUserId());
            if (user != null) {
                comment.setUser(user);
                System.out.println("[DEBUG] Set user info for comment ID: " + comment.getId() + ", username: " + user.getUsername());
            } else {
                System.out.println("[WARN] User not found for comment ID: " + comment.getId() + ", userId: " + comment.getUserId());
            }
            
            // 如果是顶级评论，获取回复数量
            if (comment.getParentId() == null || comment.getParentId() == 0) {
                int replyCount = getReplyCount(comment.getId());
                comment.setReplyCount(replyCount);
                System.out.println("[DEBUG] Set reply count for comment ID: " + comment.getId() + ", count: " + replyCount);
            }
        }
        
        System.out.println("[DEBUG] Returning " + comments.size() + " comments for promptId: " + promptId);
        return comments;
    }
    
    @Override
    public Comment addComment(Comment comment) {
        comment.setLikeCount(0);
        comment.setStatus("active");
        commentMapper.insert(comment);
        
        // 填充用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            comment.setUser(user);
        }
        
        return comment;
    }
    
    @Override
    public boolean deleteComment(Long commentId, Long userId) {
        Comment comment = getById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            return false;
        }
        
        // 逻辑删除评论及其回复
        // 使用removeById进行逻辑删除
        boolean deleted = removeById(commentId);
        
        if (deleted) {
            // 删除该评论的所有回复
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getParentId, commentId);
            List<Comment> replies = list(wrapper);
            
            for (Comment reply : replies) {
                removeById(reply.getId());
            }
        }
        
        return deleted;
    }
    
    @Override
    public List<Comment> getReplies(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId)
               .orderByAsc(Comment::getCreateTime);

        List<Comment> replies = list(wrapper);

        // 填充用户信息
        for (Comment reply : replies) {
            User user = userMapper.selectById(reply.getUserId());
            if (user != null) {
                reply.setUser(user);
            }
        }

        return replies;
    }

    @Override
    public Long getCommentUserId(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        return comment != null ? comment.getUserId() : null;
    }
    
    @Override
    public int getCommentCount(Long promptId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getPromptId, promptId);
        return Math.toIntExact(count(wrapper));
    }
    
    private int getReplyCount(Long parentId) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getParentId, parentId);
        return Math.toIntExact(count(wrapper));
    }
    
    @Override
    public boolean likeComment(Long commentId, Long userId) {
        // 检查是否已经点赞
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        
        if (commentLikeMapper.selectCount(wrapper) > 0) {
            return false;
        }
        
        // 添加点赞记录
        CommentLike commentLike = new CommentLike();
        commentLike.setCommentId(commentId);
        commentLike.setUserId(userId);
        commentLikeMapper.insert(commentLike);
        
        // 更新点赞数
        Comment comment = getById(commentId);
        if (comment != null) {
            comment.setLikeCount(comment.getLikeCount() + 1);
            updateById(comment);
        }
        
        return true;
    }
    
    @Override
    public boolean unlikeComment(Long commentId, Long userId) {
        // 删除点赞记录
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        
        int deleted = commentLikeMapper.delete(wrapper);
        if (deleted > 0) {
            // 更新点赞数
            Comment comment = getById(commentId);
            if (comment != null && comment.getLikeCount() > 0) {
                comment.setLikeCount(comment.getLikeCount() - 1);
                updateById(comment);
            }
            return true;
        }
        return false;
    }
    
    @Override
    public boolean hasLikedComment(Long commentId, Long userId) {
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getCommentId, commentId)
               .eq(CommentLike::getUserId, userId);
        return commentLikeMapper.selectCount(wrapper) > 0;
    }
    
    @Override
    public List<Long> getUserLikedCommentIds(Long userId, List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<CommentLike> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CommentLike::getUserId, userId)
               .in(CommentLike::getCommentId, commentIds);
        
        List<CommentLike> likes = commentLikeMapper.selectList(wrapper);
        return likes.stream()
                   .map(CommentLike::getCommentId)
                   .collect(Collectors.toList());
    }
    
    @Override
    public IPage<Comment> getAllComments(Long page, Long size, String keyword, String status) {
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            wrapper.like(Comment::getContent, keyword.trim());
        }
        
        if (status != null && !status.trim().isEmpty()) {
            wrapper.eq(Comment::getStatus, status.trim());
        }
        
        wrapper.orderByDesc(Comment::getCreateTime);
        
        IPage<Comment> pageObj = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(page, size);
        IPage<Comment> commentPage = page(pageObj, wrapper);
        
        // 填充用户信息、提示词标题和分类名称
        if (commentPage.getRecords() != null) {
            for (Comment comment : commentPage.getRecords()) {
                // 填充用户信息
                User user = userMapper.selectById(comment.getUserId());
                if (user != null) {
                    comment.setUser(user);
                }
                
                // 填充提示词标题和分类名称
                Prompt prompt = promptMapper.selectById(comment.getPromptId());
                if (prompt != null) {
                    comment.setPromptTitle(prompt.getTitle());
                    
                    // 填充分类名称
                    PromptCategory category = promptCategoryMapper.selectById(prompt.getCategoryId());
                    if (category != null) {
                        comment.setCategoryName(category.getName());
                    }
                }
            }
        }
        
        return commentPage;
    }
    
    @Override
    public boolean deleteCommentByAdmin(Long commentId) {
        Comment comment = getById(commentId);
        if (comment == null) {
            return false;
        }
        
        // 逻辑删除评论及其回复
        // 使用removeById进行逻辑删除，而不是手动设置deleted字段
        boolean deleted = removeById(commentId);
        
        if (deleted) {
            // 删除该评论的所有回复
            LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Comment::getParentId, commentId);
            List<Comment> replies = list(wrapper);
            
            for (Comment reply : replies) {
                removeById(reply.getId());
            }
        }
        
        return deleted;
    }
    
    @Override
    public boolean batchDeleteComments(List<Long> commentIds) {
        if (commentIds == null || commentIds.isEmpty()) {
            return false;
        }
        
        // 批量逻辑删除评论
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Comment::getId, commentIds);
        
        List<Comment> comments = list(wrapper);
        boolean allDeleted = true;
        
        for (Comment comment : comments) {
            // 使用removeById进行逻辑删除
            boolean deleted = removeById(comment.getId());
            if (!deleted) {
                allDeleted = false;
            }
            
            // 删除该评论的所有回复
            LambdaQueryWrapper<Comment> replyWrapper = new LambdaQueryWrapper<>();
            replyWrapper.eq(Comment::getParentId, comment.getId());
            List<Comment> replies = list(replyWrapper);
            
            for (Comment reply : replies) {
                removeById(reply.getId());
            }
        }
        
        return allDeleted;
    }
}