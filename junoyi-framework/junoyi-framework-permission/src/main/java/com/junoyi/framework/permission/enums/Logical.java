package com.junoyi.framework.permission.enums;

/**
 * 逻辑运算符枚举
 * 定义了基本的逻辑运算类型，包括逻辑与(AND)和逻辑或(OR)
 *
 * @author Fan
 */
public enum Logical {

    /**
     * 逻辑与运算符
     * 表示两个条件同时为真时结果才为真的逻辑运算
     */
    AND,

    /**
     * 逻辑或运算符
     * 表示两个条件中至少有一个为真时结果就为真的逻辑运算
     */
    OR
}
