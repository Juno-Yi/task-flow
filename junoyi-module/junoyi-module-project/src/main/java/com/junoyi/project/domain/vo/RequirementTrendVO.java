package com.junoyi.project.domain.vo;

import lombok.Data;

/**
 * 需求趋势 VO
 *
 * @author Fan
 */
@Data
public class RequirementTrendVO {

    /**
     * 日期
     */
    private String date;

    /**
     * 数量
     */
    private Long count;
}