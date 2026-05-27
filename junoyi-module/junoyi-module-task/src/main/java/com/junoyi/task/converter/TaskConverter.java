package com.junoyi.task.converter;

import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.TaskListVO;
import org.springframework.beans.BeanUtils;

import java.util.Date;

/**
 * 任务转换器
 *
 * @author Fan
 */
public final class TaskConverter {

    /**
     * Task PO 转换为 TaskListVO
     *
     * @param task Task PO 对象
     * @return TaskListVO
     */
    public static TaskListVO toTaskListVO(Task task) {
        if (task == null) {
            return null;
        }

        TaskListVO vo = new TaskListVO();
        BeanUtils.copyProperties(task, vo);

        // 计算是否逾期
        vo.setIsOverdue(calculateOverdue(task));

        return vo;
    }

    /**
     * 计算任务是否逾期
     *
     * @param task 任务对象
     * @return 是否逾期
     */
    private static Boolean calculateOverdue(Task task) {
        if (task == null) {
            return false;
        }

        // 如果任务已完成，不算逾期
        if (task.getStatus() != null && task.getStatus() == 4) {
            return false;
        }

        // 如果有计划结束时间，判断是否超过计划结束时间
        if (task.getPlanEndTime() != null) {
            Date now = new Date();
            return now.after(task.getPlanEndTime());
        }

        return false;
    }
}