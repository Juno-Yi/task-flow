package com.junoyi.framework.core.domain.page;

import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * 分页结果
 *
 * @author Fan
 */
@Data
public class PageResult<T> {

    /**
     * 数据列表
     */
    private List<T> list;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer current;

    /**
     * 每页数量
     */
    private Integer size;

    /**
     * 总页数
     */
    private Integer pages;

    public PageResult() {
    }

    public PageResult(List<T> list, Long total, Integer current, Integer size) {
        this.list = list;
        this.total = total;
        this.current = current;
        this.size = size;
        this.pages = (int) Math.ceil((double) total / size);
    }

    /**
     * 创建分页结果
     */
    public static <T> PageResult<T> of(List<T> list, Long total, Integer current, Integer size) {
        return new PageResult<>(list, total, current, size);
    }

    /**
     * 创建分页结果（从 PageQuery）
     */
    public static <T> PageResult<T> of(List<T> list, Long total, PageQuery query) {
        return new PageResult<>(list, total, query.getCurrent(), query.getSize());
    }

    /**
     * 创建空分页结果
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(Collections.emptyList(), 0L, 1, 10);
    }

    /**
     * 创建空分页结果（带分页参数）
     */
    public static <T> PageResult<T> empty(PageQuery query) {
        return new PageResult<>(Collections.emptyList(), 0L, query.getCurrent(), query.getSize());
    }
}
