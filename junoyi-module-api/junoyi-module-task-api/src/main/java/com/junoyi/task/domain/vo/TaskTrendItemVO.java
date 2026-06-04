package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务趋势项 VO
 *
 * @author Fan
 */
@Data
public class TaskTrendItemVO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Long count;
}

