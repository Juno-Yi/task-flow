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
    CREATE_REQUIREMENT(1, "创建需求");

    private final Integer code;

    private final String label;

    ProjectRecordType(Integer code, String label){
        this.code = code;
        this.label = label;
    }

}
