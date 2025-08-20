package com.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.platform.entity.PromptCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 提示词分类Mapper接口
 */
@Mapper
public interface PromptCategoryMapper extends BaseMapper<PromptCategory> {
}