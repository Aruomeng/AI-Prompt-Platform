package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.UserLike;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户点赞Mapper接口
 */
@Mapper
public interface UserLikeMapper extends BaseMapper<UserLike> {
}