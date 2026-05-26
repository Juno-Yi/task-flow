package com.junoyi.project.domain.vo;

import lombok.Data;

/**
 * 项目活跃度趋势 VO 数据对象
 *
 * @author Fan
 */
@Data
public class ProjectActivityTrendVO {

    /**
     * 日期
     */
    private String date;

    /**
     * 次数
     */
    private Integer count;
}