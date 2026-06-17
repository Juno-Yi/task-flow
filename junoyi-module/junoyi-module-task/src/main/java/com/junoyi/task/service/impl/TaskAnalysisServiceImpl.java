package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.TaskAnalysisOverviewVO;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.service.ITaskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * 任务分析业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class TaskAnalysisServiceImpl implements ITaskAnalysisService {

    private final TaskMapper taskMapper;

    /**
     * 获取任务分析总览统计数据
     * @return 任务分析总览统计数据
     */
    @Override
    public TaskAnalysisOverviewVO getTaskAnalysisOverview() {
        TaskAnalysisOverviewVO vo = new TaskAnalysisOverviewVO();

        // 当前月统计
        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        vo.setMonthData(buildOverviewItem(monthStart, monthEnd));

        // 当前季度统计
        int quarterStartMonth = (now.getMonthValue() - 1) / 3 * 3 + 1;
        LocalDate quarterStart = now.withMonth(quarterStartMonth).withDayOfMonth(1);
        LocalDate quarterEnd = quarterStart.plusMonths(2);
        quarterEnd = quarterEnd.withDayOfMonth(quarterEnd.lengthOfMonth());
        vo.setQuarterData(buildOverviewItem(quarterStart, quarterEnd));

        // 当前年度统计
        LocalDate yearStart = now.withMonth(1).withDayOfMonth(1);
        LocalDate yearEnd = now.withMonth(12).withDayOfMonth(31);
        vo.setYearData(buildOverviewItem(yearStart, yearEnd));

        // 全部数据（不限时间范围）
        vo.setAllData(buildOverviewItem(null, null));

        return vo;
    }

    /**
     * 构建指定时间范围内的统计项
     *
     * @param startDate 开始日期（null 表示不限）
     * @param endDate   结束日期（null 表示不限）
     * @return 统计项
     */
    private TaskAnalysisOverviewVO.TaskAnalysisOverviewItem buildOverviewItem(LocalDate startDate, LocalDate endDate) {
        Date start = null;
        Date end = null;

        if (startDate != null) {
            start = Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (endDate != null) {
            end = Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        }

        // 查询时间范围内的所有未删除任务
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<Task>()
                .eq(Task::getDelFlag, false)
                .ge(start != null, Task::getCreateTime, start)
                .le(end != null, Task::getCreateTime, end)
                .select(Task::getStatus);

        List<Task> tasks = taskMapper.selectList(wrapper);

        // 按状态统计
        int pendingCount = 0;
        int ongoingCount = 0;
        int reviewCount = 0;
        int rejectedCount = 0;
        int completedCount = 0;

        for (Task task : tasks) {
            if (task.getStatus() == null) continue;
            switch (task.getStatus()) {
                case 0 -> pendingCount++;
                case 1 -> ongoingCount++;
                case 2 -> reviewCount++;
                case 3 -> rejectedCount++;
                case 4 -> completedCount++;
                default -> {}
            }
        }

        TaskAnalysisOverviewVO.TaskAnalysisOverviewItem item = new TaskAnalysisOverviewVO.TaskAnalysisOverviewItem();
        item.setPendingTaskCount(pendingCount);
        item.setOngoingTaskCount(ongoingCount);
        item.setReviewTaskCount(reviewCount);
        item.setRejectedTaskCount(rejectedCount);
        item.setCompletedTaskCount(completedCount);

        return item;
    }
}