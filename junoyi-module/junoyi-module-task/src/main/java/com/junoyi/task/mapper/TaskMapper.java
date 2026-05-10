package com.junoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.task.domain.po.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务 Mapper
 *
 * @author Fan
 */
@Mapper
public interface TaskMapper extends BaseMapper<Task> {
}
