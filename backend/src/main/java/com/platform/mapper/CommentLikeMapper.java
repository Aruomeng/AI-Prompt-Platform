package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.CommentLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 评论点赞Mapper
 */
@Mapper
public interface CommentLikeMapper extends BaseMapper<CommentLike> {
}