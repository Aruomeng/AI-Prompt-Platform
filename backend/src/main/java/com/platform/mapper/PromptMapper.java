package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.entity.Prompt;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 提示词Mapper接口
 */
@Mapper
public interface PromptMapper extends BaseMapper<Prompt> {
    
    /**
     * 获取用户收藏的提示词分页
     */
    IPage<Prompt> selectUserFavoritesPage(IPage<Prompt> page, @Param("userId") Long userId);
}