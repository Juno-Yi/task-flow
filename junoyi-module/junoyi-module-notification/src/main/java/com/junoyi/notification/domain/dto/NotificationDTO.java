package com.junoyi.notification.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 消息通知 DTO 数据对象
 *
 * @author Fan
 */
@Data
public class NotificationDTO {

    /**
     * 主键ID(修改时候使用）
     */
    private Long id;

    /**
     * 通知标题
     */
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型
     */
    private Integer type;

    /**
     * 状态（0-草稿 1-已发布）
     */
    private Integer status;

    /**
     * 目标范围类型（0-全部 1-部门 2-角色 3-指定用户）
     */
    private Integer targetType;

    /**
     * 目标ID列表（部门ID / 角色ID / 用户ID）
     * 当 targetType=0（全部）时可不传
     */
    private List<Long> targetIds;
}