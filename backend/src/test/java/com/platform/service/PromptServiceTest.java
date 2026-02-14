package com.platform.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.entity.Prompt;
import com.platform.mapper.PromptMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * PromptService单元测试
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("提示词服务测试")
class PromptServiceTest {
    
    @Mock
    private PromptMapper promptMapper;
    
    @InjectMocks
    private PromptService promptService;
    
    private Prompt testPrompt;
    
    @BeforeEach
    void setUp() {
        testPrompt = new Prompt();
        testPrompt.setId(1L);
        testPrompt.setTitle("AI写作助手");
        testPrompt.setContent("你是一个专业的写作助手...");
        testPrompt.setDescription("通用AI写作助手");
        testPrompt.setUserId(1L);
        testPrompt.setCategoryId(1L);
        testPrompt.setStatus("APPROVED");
        testPrompt.setUsageCount(10);
        testPrompt.setLikeCount(5);
        testPrompt.setFavoriteCount(3);
        testPrompt.setCreateTime(LocalDateTime.now());
        testPrompt.setUpdateTime(LocalDateTime.now());
    }
    
    @Test
    @DisplayName("创建提示词")
    void testCreatePrompt() {
        // 准备
        when(promptMapper.insert(any(Prompt.class)))
            .thenReturn(1);
        when(promptMapper.selectById(any()))
            .thenReturn(testPrompt);
        
        // 执行
        Prompt result = promptService.createPrompt(testPrompt);
        
        // 验证
        assertNotNull(result);
        assertEquals("AI写作助手", result.getTitle());
        verify(promptMapper, times(1)).insert(any(Prompt.class));
    }
    
    @Test
    @DisplayName("更新提示词")
    void testUpdatePrompt() {
        // 准备
        testPrompt.setTitle("更新后的标题");
        when(promptMapper.updateById(any(Prompt.class)))
            .thenReturn(1);
        
        // 执行
        boolean result = promptService.updatePrompt(testPrompt);
        
        // 验证
        assertTrue(result);
        verify(promptMapper, times(1)).updateById(any(Prompt.class));
    }
    
    @Test
    @DisplayName("删除提示词 - 拥有者删除")
    void testDeletePromptAsOwner() {
        // 准备
        when(promptMapper.selectById(1L))
            .thenReturn(testPrompt);
        when(promptMapper.updateById(any(Prompt.class)))
            .thenReturn(1);
        
        // 执行
        boolean result = promptService.deletePrompt(1L, 1L);
        
        // 验证
        assertTrue(result);
    }
    
    @Test
    @DisplayName("删除提示词 - 非拥有者无法删除")
    void testDeletePromptAsNonOwner() {
        // 准备
        when(promptMapper.selectById(1L))
            .thenReturn(testPrompt);
        
        // 执行
        boolean result = promptService.deletePrompt(1L, 2L);
        
        // 验证
        assertFalse(result);
        verify(promptMapper, never()).updateById(any(Prompt.class));
    }
    
    @Test
    @DisplayName("点赞提示词")
    void testLikePrompt() {
        // 准备
        when(promptMapper.selectById(1L))
            .thenReturn(testPrompt);
        when(promptMapper.updateById(any(Prompt.class)))
            .thenReturn(1);
        
        // 执行
        boolean result = promptService.likePrompt(1L, 2L);
        
        // 验证
        assertTrue(result);
        assertEquals(6, testPrompt.getLikeCount());
    }
    
    @Test
    @DisplayName("增加使用次数")
    void testIncrementUsageCount() {
        // 准备
        when(promptMapper.selectById(1L))
            .thenReturn(testPrompt);
        when(promptMapper.updateById(any(Prompt.class)))
            .thenReturn(1);
        
        // 执行
        promptService.incrementUsageCount(1L);
        
        // 验证
        assertEquals(11, testPrompt.getUsageCount());
        verify(promptMapper, times(1)).updateById(any(Prompt.class));
    }
    
    @Test
    @DisplayName("获取已批准的提示词分页列表")
    void testGetApprovedPromptsPage() {
        // 准备
        Page<Prompt> page = new Page<>(1, 10);
        page.setRecords(java.util.List.of(testPrompt));
        page.setTotal(1);
        
        when(promptMapper.selectPage(any(), any()))
            .thenReturn(page);
        
        // 执行
        IPage<Prompt> result = promptService.getApprovedPromptsPage(1L, 10L, null, null);
        
        // 验证
        assertNotNull(result);
        assertEquals(1, result.getRecords().size());
        assertEquals(1, result.getTotal());
    }
}
