package com.junoyi.project.event;


import com.junoyi.framework.event.domain.BaseEvent;
import com.junoyi.project.enums.ProjectRecordTargetType;
import com.junoyi.project.enums.ProjectRecordType;
import lombok.Getter;

/**
 * 项目动态记录事件
 *
 * @author Fan
 */
@Getter
public class ProjectRecordEvent extends BaseEvent {

    /**
     * 项目ID
     */
    private final Long projectId;

    /**
     * 操作者ID
     */
    private final Long operatorId;

    /**
     * 动态类型
     */
    private final Integer type;

    /**
     * 目标类型
     */
    private final Integer targetType;

    /**
     * 目标ID
     */
    private final Long targetId;

    /**
     * 动态内容
     */
    private final String content;

    /**
     * 项目动态记录事件构建
     * @param projectId 项目ID
     * @param operatorId 操作者ID
     * @param type 操作类型
     * @param targetType 目标类型
     * @param targetId 目标ID
     * @param content 操作内容描述
     */
    public ProjectRecordEvent(Long projectId,
                               Long operatorId,
                               ProjectRecordType type,
                               ProjectRecordTargetType targetType,
                               Long targetId,
                               String content) {
        this.projectId = projectId;
        this.operatorId = operatorId;
        this.type = type.getCode();
        this.targetType = targetType.getCode();
        this.targetId = targetId;
        this.content = content;
    }

    /**
     * 项目动态记录事件构建
     * @param projectId 项目ID
     * @param operatorId 操作者ID
     * @param type 操作类型
     * @param targetType 目标类型
     * @param content 操作内容描述
     */
    public ProjectRecordEvent(Long projectId,
                              Long operatorId,
                              ProjectRecordType type,
                              ProjectRecordTargetType targetType,
                              String content) {
        this.projectId = projectId;
        this.operatorId = operatorId;
        this.type = type.getCode();
        this.targetType = targetType.getCode();
        this.content = content;
    }
}