package com.platform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 提示词实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("prompts")
public class Prompt {
    
    /**
     * 提示词ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    
    /**
     * 标题
     */
    @TableField("title")
    private String title;
    
    /**
     * 内容
     */
    @TableField("content")
    private String content;
    
    /**
     * 描述
     */
    @TableField("description")
    private String description;
    
    /**
     * 分类ID
     */
    @TableField("category_id")
    private Long categoryId;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 标签
     */
    @TableField("tags")
    private String tags;
    
    /**
     * 使用次数（对应数据库中的view_count）
     */
    @TableField("view_count")
    private Integer usageCount;
    
    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;
    
    /**
     * 收藏数
     */
    @TableField("favorite_count")
    private Integer favoriteCount;
    
    /**
     * 状态：PENDING/APPROVED/REJECTED
     */
    @TableField("status")
    private String status;
    
    /**
     * 审核意见（对应数据库中的audit_remark）
     */
    @TableField("audit_remark")
    private String reviewComment;
    
    /**
     * 审核时间
     */
    @TableField("audit_time")
    private LocalDateTime auditTime;
    
    /**
     * 审核用户ID
     */
    @TableField("audit_user_id")
    private Long auditUserId;
    
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
     * 分类名称（非数据库字段）
     */
    @TableField(exist = false)
    private String categoryName;
    
    /**
     * 用户信息（非数据库字段）
     */
    @TableField(exist = false)
    private User user;
}