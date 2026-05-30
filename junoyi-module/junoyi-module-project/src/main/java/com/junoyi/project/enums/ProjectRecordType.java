package com.junoyi.project.enums;

import lombok.Getter;

/**
 * 项目动态记录类型枚举
 *
 * @author Fan
 */
@Getter
public enum ProjectRecordType {

    /**
     * 创建需求
     */
    CREATE_REQUIREMENT(1, "创建需求"),

    /**
     * 更新需求
     */
    UPDATE_REQUIREMENT(2,"更新需求"),

    /**
     * 删除需求
     */
    DELETE_REQUIREMENT(3,"删除需求"),

    /**
     * 创建里程碑
     */
    CREATE_MILESTONE(4,"创建里程碑"),

    /**
     * 更新里程碑
     */
    UPDATE_MILESTONE(5,"更新里程碑"),

    /**
     * 删除里程碑
     */
    DELETE_MILESTONE(6,"删除里程碑"),

    /**
     * 完成里程碑
     */
    COMPLETE_MILESTONE(7,"完成里程碑"),

    /**
     * 创建任务
     */
    CREATE_TASK(8, "创建任务"),

    /**
     * 更新任务
     */
    UPDATE_TASK(9, "更新任务");

    private final Integer code;

    private final String label;

    ProjectRecordType(Integer code, String label){
        this.code = code;
        this.label = label;
    }

}
