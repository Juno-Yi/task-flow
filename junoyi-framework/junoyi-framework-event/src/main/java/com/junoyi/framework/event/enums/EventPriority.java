package com.junoyi.framework.event.enums;

/**
 * 事件优先级枚举
 *
 * @author Fan
 */
public enum EventPriority {
    /**
     * 优先级枚举类，定义了不同级别的优先级常量
     * 每个枚举值包含一个整数类型的优先级编码
     * 优先级从LOWEST(最低)到MONITOR(监控)，数值依次递增
     */
    LOWEST(0),
    LOW(1),
    NORMAL(2),
    HIGH(3),
    HIGHEST(4),
    MONITOR(5);


    private final int level;

    /**
     * 构造函数，初始化优先级等级
     *
     * @param level 优先级等级数值
     */
    EventPriority(int level) {
        this.level = level;
    }

    /**
     * 获取优先级等级数值
     *
     * @return 优先级等级数值
     */
    public int getLevel() {
        return level;
    }

}
