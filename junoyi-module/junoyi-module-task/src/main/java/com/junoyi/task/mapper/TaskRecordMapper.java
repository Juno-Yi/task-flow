package com.junoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.task.domain.po.TaskRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务记录 Mapper
 *
 * @author Fan
 */
@Mapper
public interface TaskRecordMapper extends BaseMapper<TaskRecord> {
}
