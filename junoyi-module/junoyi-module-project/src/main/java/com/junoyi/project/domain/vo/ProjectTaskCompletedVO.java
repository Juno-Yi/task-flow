package com.junoyi.project.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * 项目任务完成情况 VO
 *
 * @author Fan
 */
@Data
public class ProjectTaskCompletedVO {

    /**
     * 近七天的完成数据
     */
    private List<TaskTrendVO> sevenDayList;

    /**
     * 近30天的完成数据
     */
    private List<TaskTrendVO> thirtyDayList;

    /**
     * 近90天的完成数据
     */
    private List<TaskTrendVO> ninetyDayList;
}