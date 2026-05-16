package com.junoyi.task.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务列表 VO 数据对象
 *
 * @author Fanhuo
 */
@Data
public class TaskListVO {

    /**
     * 任务主键ID
     */
    private Long id;

    /**
     * 任务标题
     */
    private String title;

    /**
     * 任务状态
     */
    private Integer status;

    private String statusLabel;

    private String statusType;

    /**
     * 任务优先级
     */
    private Integer priority;

    private String priorityLabel;

    private String priorityType;

    /**
     * 任务执行人列表
     */
    private List<TaskUser> taskUserList;

    /**
     * 项目负责人
     */
    private OwnerUser ownerUser;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 截止时间
     */
    private Date DueTime;

    /**
     * 完成时间
     */
    private Date finishTime;

    /**
     * 是否逾期（计算字段，非数据库存储）
     */
    private Boolean isOverdue;

    /**
     * 最近提交时间
     */
    private Date latestSubmitTime;

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