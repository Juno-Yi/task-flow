package com.junoyi.task.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.junoyi.task.domain.po.TaskUser;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务-用户 Mapper
 *
 * @author Fan
 */
@Mapper
public interface TaskUserMapper extends BaseMapper<TaskUser> {

    /**
     * 根据任务ID删除任务用户关系
     * @param taskId 任务ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM task_user WHERE task_id = #{taskId}")
    int deleteByTaskId(Long taskId);
}
