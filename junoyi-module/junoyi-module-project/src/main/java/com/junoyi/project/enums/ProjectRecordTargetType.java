package com.junoyi.project.enums;

import lombok.Getter;

/**
 * 项目动态记录目标类型枚举
 *
 * @author Fan
 */
@Getter
public enum ProjectRecordTargetType {

    /**
     * 需求
     */
    REQUIREMENT(1, "需求"),

    /**
     * 里程碑
     */
    MILESTONE(2,"里程碑"),

    /**
     * 任务
     */
    TASK(3,"任务");

    private final Integer code;

    private final String label;

    ProjectRecordTargetType(Integer code, String label){
        this.code = code;
        this.label = label;
    }
}
