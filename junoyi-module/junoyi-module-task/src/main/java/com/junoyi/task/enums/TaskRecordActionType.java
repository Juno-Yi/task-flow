package com.junoyi.task.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 任务记录操作类型枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum TaskRecordActionType {

    /**
     * 提交任务
     */
    SUBMIT(1, "提交任务"),

    /**
     * 驳回任务
     */
    REJECT(2, "驳回任务"),

    /**
     * 审核通过
     */
    APPROVE(3, "审核通过"),

    /**
     * 创建任务
     */
    CREATE(4, "创建任务"),

    /**
     * 更新任务
     */
    UPDATE(5, "更新任务"),

    /**
     * 删除任务
     */
    DELETE(6, "删除任务"),

    /**
     * 开始任务
     */
    START(7, "开始任务"),

    /**
     * 完成任务
     */
    COMPLETE(8, "完成任务"),

    /**
     * 关闭任务
     */
    CLOSE(9, "关闭任务"),

    /**
     * 重新打开任务
     */
    REOPEN(10, "重新打开任务");

    /**
     * 操作类型值
     */
    private final Integer value;

    /**
     * 操作类型标签
     */
    private final String label;

    /**
     * 根据值获取枚举
     *
     * @param value 操作类型值
     * @return 枚举对象，如果不存在返回 null
     */
    public static TaskRecordActionType getByValue(Integer value) {
        if (value == null) {
            return null;
        }
        for (TaskRecordActionType type : values()) {
            if (type.getValue().equals(value)) {
                return type;
            }
        }
        return null;
    }

    /**
     * 根据值获取标签
     *
     * @param value 操作类型值
     * @return 标签，如果不存在返回默认值
     */
    public static String getLabelByValue(Integer value) {
        TaskRecordActionType type = getByValue(value);
        return type != null ? type.getLabel() : "未知操作";
    }
}

