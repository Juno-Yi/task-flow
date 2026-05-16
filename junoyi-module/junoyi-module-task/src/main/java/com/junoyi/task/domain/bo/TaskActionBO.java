package com.junoyi.task.domain.bo;

import com.junoyi.task.domain.dto.BaseTaskActionDTO;
import lombok.Builder;
import lombok.Data;

/**
 * 任务操作业务数据 BO 数据对象
 *
 * @author Fan
 */
@Data
@Builder
public class TaskActionBO {

    /**
     * 业务操作用户对象
     */
    private Long userId;

    /**
     * 任务操作类型
     */
    private Integer taskActionType;

    /**
     * 任务传输数据
     */
    private BaseTaskActionDTO dto;
}