package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 项目概览数据 VO 数据对象
 *
 * @author Fan
 */
@Data
public class ProjectOverviewVO {

    /**
     * 近一年的项目活跃度
     */
    private List<ProjectActivityTrendVO> projectActivityTrend;

    /**
     * 项目关键指标数据
     */
    private ProjectKeyDataVO projectKeyData;

    /**
     * 项目需求情况饼图数据
     */
    private List<ProjectRequirementSituationVO> projectRequirementSituation;

    /**
     * 项目需求完成趋势数据
     */
    private ProjectRequirementCompletedVO projectRequirementCompletedVO;
}

