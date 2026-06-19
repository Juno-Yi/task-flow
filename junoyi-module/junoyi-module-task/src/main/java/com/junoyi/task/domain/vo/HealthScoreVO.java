package com.junoyi.task.domain.vo;

import lombok.Data;

/**
 * 任务健康分 VO
 *
 * @author Fan
 */
@Data
public class HealthScoreVO {

    private Double monthData;

    private Double quarterData;

    private Double yearData;

    private Double allData;
}