package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 项目需求完成情况 VO
 *
 * @author Fan
 */
@Data
public class ProjectRequirementCompletedVO {

    /**
     * 最近7天
     */
    private List<RequirementTrendVO> sevenDayList;

    /**
     * 最近30天
     */
    private List<RequirementTrendVO> thirtyDayList;

    /**
     * 最近90天
     */
    private List<RequirementTrendVO> ninetyDayList;

}