package com.junoyi.task.domain.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 任务列表详情 VO
 *
 * @author Fan
 */
@Data
public class TaskListDetailVO {

    /**
     * 任务ID主键
     */
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

    private String statusLabel;

    private String statusType;

    /**
     * 任务优先级
     */
    private Integer priority;

    private String priorityLabel;

    private String priorityType;

    /**
     * 计划开始时间
     */
    private Date planStartTime;

    /**
     * 计划结束时间
     */
    private Date planEndTime;

    /**
     * 实际开始时间
     */
    private Date startTime;

    /**
     * 实际结束时间
     */
    private Date endTime;

    /**
     * 是否逾期（计算字段，非数据库存储）
     */
    private Boolean isOverdue;

    /**
     * 任务执行人列表
     */
    private List<TaskUser> taskUserList;

    /**
     * 项目负责人
     */
    private OwnerUser ownerUser;

    /**
     * 创建者
     */
    private String createBy;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新者
     */
    private String updateBy;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 任务的项目ID
     */
    private Long projectId;

    /**
     * 任务记录列表
     */
    private List<RecordItem> recordList;

    /**
     * 最近一次提交记录
     */
    private RecordItem latestSubmitRecord;

    /**
     * 最近一次驳回记录
     */
    private RecordItem latestRejectRecord;

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

    /**
     * 任务记录
     */
    @Data
    public static class RecordItem {
        private Long id;
        private Long taskId;
        private Long operatorId;
        private String operatorName;
        private String operatorAvatar;
        private Integer actionType;
        private String actionTypeLabel;
        private String remark;
        private Date createTime;
        private List<AttachmentItem> attachments;
    }

    /**
     * 记录附件
     */
    @Data
    public static class AttachmentItem {
        private Long id;
        private Long taskId;
        private Long recordId;
        private String fileName;
        private String fileUrl;
    }
}