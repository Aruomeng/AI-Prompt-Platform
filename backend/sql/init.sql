-- 创建数据库
CREATE DATABASE IF NOT EXISTS ai_prompt_platform DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE ai_prompt_platform;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(100) NOT NULL COMMENT '密码',
    `email` varchar(100) DEFAULT NULL COMMENT '邮箱',
    `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
    `avatar` varchar(255) DEFAULT NULL COMMENT '头像URL',
    `role` varchar(20) DEFAULT 'USER' COMMENT '角色：USER/ADMIN',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除：0-未删除，1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 提示词分类表
CREATE TABLE IF NOT EXISTS `prompt_category` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name` varchar(50) NOT NULL COMMENT '分类名称',
    `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
    `sort_order` int DEFAULT '0' COMMENT '排序',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词分类表';

-- 提示词表
CREATE TABLE IF NOT EXISTS `prompt` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '提示词ID',
    `title` varchar(100) NOT NULL COMMENT '标题',
    `content` text NOT NULL COMMENT '提示词内容',
    `description` varchar(500) DEFAULT NULL COMMENT '描述',
    `category_id` bigint DEFAULT NULL COMMENT '分类ID',
    `tags` varchar(255) DEFAULT NULL COMMENT '标签，逗号分隔',
    `user_id` bigint NOT NULL COMMENT '创建用户ID',
    `usage_count` int DEFAULT '0' COMMENT '使用次数',
    `like_count` int DEFAULT '0' COMMENT '点赞数',
    `favorite_count` int DEFAULT '0' COMMENT '收藏数',
    `status` tinyint DEFAULT '1' COMMENT '状态：1-正常，0-禁用',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_category_id` (`category_id`),
    KEY `idx_status` (`status`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='提示词表';

-- 用户点赞表
CREATE TABLE IF NOT EXISTS `user_like` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `prompt_id` bigint NOT NULL COMMENT '提示词ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_prompt` (`user_id`, `prompt_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_prompt_id` (`prompt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户点赞表';

-- 用户收藏表
CREATE TABLE IF NOT EXISTS `user_favorite` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `prompt_id` bigint NOT NULL COMMENT '提示词ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_prompt` (`user_id`, `prompt_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_prompt_id` (`prompt_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- 评论表
CREATE TABLE IF NOT EXISTS `comment` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
    `content` text NOT NULL COMMENT '评论内容',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `prompt_id` bigint NOT NULL COMMENT '提示词ID',
    `parent_id` bigint DEFAULT NULL COMMENT '父评论ID',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` tinyint DEFAULT '0' COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_prompt_id` (`prompt_id`),
    KEY `idx_parent_id` (`parent_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评论表';

-- 插入初始分类数据
INSERT INTO `prompt_category` (`name`, `description`, `sort_order`) VALUES
('写作助手', '帮助用户进行各种写作任务', 1),
('编程助手', '代码生成、调试、解释等', 2),
('学习助手', '学习计划、知识问答等', 3),
('生活助手', '日常生活相关的提示词', 4),
('工作助手', '职场、办公相关的提示词', 5),
('创意灵感', '创意写作、头脑风暴等', 6);

-- 插入测试用户
INSERT INTO `user` (`username`, `password`, `email`, `nickname`, `role`) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'admin@example.com', '管理员', 'ADMIN'),
('testuser', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iOEcalwu', 'test@example.com', '测试用户', 'USER');

-- 插入测试提示词
INSERT INTO `prompt` (`title`, `content`, `description`, `category_id`, `tags`, `user_id`, `usage_count`, `like_count`, `favorite_count`) VALUES
('AI写作助手', '你是一个专业的写作助手，能够帮助用户进行各种写作任务，包括但不限于：文章写作、报告撰写、邮件起草、创意写作等。请根据用户的具体需求提供专业的写作建议和内容。', '通用AI写作助手提示词', 1, '写作,助手,通用', 1, 100, 50, 30),
('代码解释器', '你是一个专业的代码解释器，能够：
1. 解释代码的功能和工作原理
2. 分析代码的优缺点
3. 提供改进建议
4. 回答关于代码的任何问题

请用简洁明了的语言解释以下代码：', '帮助理解和解释代码', 2, '编程,代码解释,开发', 1, 80, 40, 25),
('学习计划制定', '你是一个专业的学习规划师，请根据用户的学习目标、时间安排和当前水平，制定个性化的学习计划。包括：
1. 学习目标设定
2. 时间安排
3. 资源推荐
4. 进度跟踪方法
5. 复习计划', '制定个性化学习计划', 3, '学习,计划,教育', 2, 60, 35, 20);