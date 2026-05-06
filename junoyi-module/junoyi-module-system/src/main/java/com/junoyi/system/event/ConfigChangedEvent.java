package com.junoyi.system.event;

import com.junoyi.framework.event.domain.BaseEvent;
import lombok.Getter;

/**
 * 系统参数配置变更事件
 *
 * @author Fan
 */
@Getter
public class ConfigChangedEvent extends BaseEvent {

    /**
     * 配置键名
     */
    private final String configKey;

    /**
     * 旧值
     */
    private final String oldValue;

    /**
     * 新值
     */
    private final String newValue;

    /**
     * 操作类型：ADD-新增, UPDATE-更新, DELETE-删除
     */
    private final OperationType operationType;

    /**
     * 操作人
     */
    private final String operator;

    public ConfigChangedEvent(Object source, String configKey, String oldValue, String newValue,
                              OperationType operationType, String operator) {
        super(source);
        this.configKey = configKey;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.operationType = operationType;
        this.operator = operator;
    }

    /**
     * 操作类型枚举
     */
    public enum OperationType {
        /**
         * 新增
         */
        ADD,
        /**
         * 更新
         */
        UPDATE,
        /**
         * 删除
         */
        DELETE
    }
}
