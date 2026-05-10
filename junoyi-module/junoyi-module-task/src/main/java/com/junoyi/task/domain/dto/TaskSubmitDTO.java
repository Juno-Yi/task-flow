package com.junoyi.task.domain.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 任务提交 DTO 数据对象
 *
 * @author Fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskSubmitDTO extends BaseTaskActionDTO{

    /**
     *  任务提交附件
     */
    private List<Attachment> attachments;

    /**
     * 任务提交附件
     */
    @Data
    public static class Attachment {

        /**
         * 文件名称
         */
        private String fileName;

        /**
         * 文件URL
         */
        private String fileUrl;
    }
}