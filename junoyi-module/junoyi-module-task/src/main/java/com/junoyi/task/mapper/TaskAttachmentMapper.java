package com.junoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.task.domain.po.TaskAttachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务附件 Mapper
 *
 * @author Fan
 */
@Mapper
public interface TaskAttachmentMapper extends BaseMapper<TaskAttachment> {
}
