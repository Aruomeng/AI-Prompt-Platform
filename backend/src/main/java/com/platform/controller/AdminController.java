package com.platform.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.platform.common.PageResult;
import com.platform.common.Result;
import com.platform.entity.Comment;
import com.platform.entity.Prompt;
import com.platform.service.PromptService;
import com.platform.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 管理员控制器
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin
public class AdminController {

    @Autowired
    private PromptService promptService;

    @Autowired
    private CommentService commentService;

    /**
     * 获取待审核的提示词列表
     */
    @GetMapping("/prompts/pending")
    public Result<PageResult<Prompt>> getPendingPrompts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size) {
        
        IPage<Prompt> pageResult = promptService.getPendingPrompts(page, size);
        PageResult<Prompt> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }

    /**
     * 获取所有提示词列表（管理用）
     */
    @GetMapping("/prompts")
    public Result<PageResult<Prompt>> getAllPrompts(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        IPage<Prompt> pageResult = promptService.getAllPrompts(page, size, keyword, status);
        PageResult<Prompt> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }

    /**
     * 审核提示词
     */
    @PostMapping("/prompts/{id}/audit")
    public Result<Void> auditPrompt(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String reviewComment,
            @RequestAttribute("userId") Long adminId) {
        
        boolean success = promptService.auditPrompt(id, status, reviewComment, adminId);
        return success ? Result.success() : Result.error("审核失败");
    }

    /**
     * 删除提示词（管理员强制删除）
     */
    @DeleteMapping("/prompts/{id}")
    public Result<Void> deletePrompt(@PathVariable Long id) {
        boolean success = promptService.deletePromptByAdmin(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 获取所有评论（管理用）
     */
    @GetMapping("/comments")
    public Result<PageResult<Comment>> getAllComments(
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status) {
        
        IPage<Comment> pageResult = commentService.getAllComments(page, size, keyword, status);
        PageResult<Comment> result = new PageResult<>();
        result.setTotal(pageResult.getTotal());
        result.setRecords(pageResult.getRecords());
        result.setCurrent(pageResult.getCurrent());
        result.setSize(pageResult.getSize());
        result.setPages(pageResult.getPages());
        
        return Result.success(result);
    }

    /**
     * 删除评论（管理员强制删除）
     */
    @DeleteMapping("/comments/{id}")
    public Result<Void> deleteComment(@PathVariable Long id) {
        boolean success = commentService.deleteCommentByAdmin(id);
        return success ? Result.success() : Result.error("删除失败");
    }

    /**
     * 批量删除评论
     */
    @DeleteMapping("/comments/batch")
    public Result<Void> batchDeleteComments(@RequestBody List<Long> ids) {
        boolean success = commentService.batchDeleteComments(ids);
        return success ? Result.success() : Result.error("批量删除失败");
    }
}