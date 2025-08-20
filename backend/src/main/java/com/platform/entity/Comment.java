package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 评论实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("comments")
public class Comment {
    
    /**
     * 评论ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 提示词ID
     */
    @TableField("prompt_id")
    private Long promptId;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 父评论ID
     */
    @TableField("parent_id")
    private Long parentId;
    
    /**
     * 评论内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;
    
    /**
     * 状态：active/inactive
     */
    @TableField("status")
    private String status;
    
    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    @TableField("deleted")
    private Integer deleted;
    
    /**
     * 用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;

    /**
     * 回复数量（非数据库字段）
     */
    @TableField(exist = false)
    private Integer replyCount;

    /**
     * 提示词标题（非数据库字段）
     */
    @TableField(exist = false)
    private String promptTitle;

    /**
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
}