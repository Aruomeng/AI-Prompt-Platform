package com.platform.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.platform.entity.PromptCategory;
import com.platform.mapper.PromptCategoryMapper;
import com.platform.service.PromptCategoryService;
import org.springframework.stereotype.Service;

/**
 * 提示词分类服务实现类
 */
@Service
public class PromptCategoryServiceImpl extends ServiceImpl<PromptCategoryMapper, PromptCategory> implements PromptCategoryService {
}