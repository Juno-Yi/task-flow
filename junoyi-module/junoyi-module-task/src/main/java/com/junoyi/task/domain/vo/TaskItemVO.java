package com.junoyi.task.domain.vo;


import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务物品 VO
 *
 * 返回给我的任务页面的任务数据对象
 *
 * @author Fan
 */
@Data
public class TaskItemVO {

    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务描述
     */
    private String description;

    /**
     * 任务状态
     */
    private Integer status;

    /**
     * 任务优先级
     */
    private Integer priority;

    /**
     * 项目负责人
     */
    private OwnerUser ownerUser;

    /**
     * 任务执行人列表
     */
    private List<TaskUser> taskUserList;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date DueTime;

    /**
     * 是否逾期（计算字段，非数据库存储）
     */
    private Boolean isOverdue;

    /**
     * 备注
     */
    private String remark;

    /**
     * 负责人
     */
    @Data
    public static class OwnerUser {
        private  Long userId;
        private String avatar;
        private String nickName;
    }

    /**
     * 任务执行人
     */
    @Data
    public static class TaskUser {

        private Long userId;

        private String avatar;

        private String nickName;
    }

}