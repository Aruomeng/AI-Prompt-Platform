package com.platform.common;

import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {
    
    /**
     * 总记录数
     */
    private Long total;
    
    /**
     * 当前页数据
     */
    private List<T> records;
    
    /**
     * 当前页码
     */
    private Long current;
    
    /**
     * 每页大小
     */
    private Long size;
    
    /**
     * 总页数
     */
    private Long pages;
    
    public PageResult() {}
    
    public PageResult(Long total, List<T> records) {
        this.total = total;
        this.records = records;
    }
    
    public PageResult(Long total, List<T> records, Long current, Long size, Long pages) {
        this.total = total;
        this.records = records;
        this.current = current;
        this.size = size;
        this.pages = pages;
    }
}