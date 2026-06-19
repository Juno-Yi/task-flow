package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.TaskAnalysisVO;
import com.junoyi.task.domain.vo.TaskCoreKpiVO;
import com.junoyi.task.domain.vo.TaskStatusOverviewVO;
import com.junoyi.task.mapper.TaskMapper;
import com.junoyi.task.service.ITaskAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
     * 获取任务分析综合数据
     * @return 任务分析综合数据
     */
    @Override
    public TaskAnalysisVO getTaskAnalysis() {
        TaskAnalysisVO vo = new TaskAnalysisVO();
        vo.setStatusOverview(buildStatusOverview());
        vo.setCoreKpi(buildCoreKpi());
        return vo;
    }

    /**
     * 构建核心KPI数据（基于当前维度为"本月"）
     */
    private TaskCoreKpiVO buildCoreKpi() {
        TaskCoreKpiVO kpi = new TaskCoreKpiVO();

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());

        Date start = Date.from(monthStart.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(monthEnd.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        // 查询本期所有未删除任务
        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getDelFlag, false)
                        .ge(Task::getCreateTime, start)
                        .le(Task::getCreateTime, end)
        );

        int totalCount = tasks.size();
        int completedCount = 0;
        int overdueCount = 0;
        long totalProcessMillis = 0;
        int processedCount = 0;
        Date nowDate = new Date();

        for (Task task : tasks) {
            // 已完成
            if (Integer.valueOf(4).equals(task.getStatus())) {
                completedCount++;
                // 计算处理时长：实际结束时间 - 实际开始时间
                if (task.getStartTime() != null && task.getEndTime() != null) {
                    totalProcessMillis += task.getEndTime().getTime() - task.getStartTime().getTime();
                    processedCount++;
                }
            }

            // 逾期：未完成且已超过计划结束时间
            if (!Integer.valueOf(4).equals(task.getStatus())
                    && task.getPlanEndTime() != null
                    && nowDate.after(task.getPlanEndTime())) {
                overdueCount++;
            }
        }

        // 完成率
        double completionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0;
        kpi.setCompletionRate(Math.round(completionRate * 10) / 10.0);

        // 逾期任务数
        kpi.setOverdueTaskCount(overdueCount);

        // 平均处理时长（天）
        double avgDays = processedCount > 0
                ? (double) totalProcessMillis / processedCount / TimeUnit.DAYS.toMillis(1)
                : 0;
        kpi.setAvgProcessDays(Math.round(avgDays * 10) / 10.0);

        // 本期新增任务数
        kpi.setNewTaskCount(totalCount);

        return kpi;
    }

    /**
     * 构建任务状态总览数据
     */
    private TaskStatusOverviewVO buildStatusOverview() {
        TaskStatusOverviewVO overview = new TaskStatusOverviewVO();

        LocalDate now = LocalDate.now();

        // 当前月统计
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        overview.setMonthData(buildOverviewItem(monthStart, monthEnd));

        // 当前季度统计
        int quarterStartMonth = (now.getMonthValue() - 1) / 3 * 3 + 1;
        LocalDate quarterStart = now.withMonth(quarterStartMonth).withDayOfMonth(1);
        LocalDate quarterEnd = quarterStart.plusMonths(2);
        quarterEnd = quarterEnd.withDayOfMonth(quarterEnd.lengthOfMonth());
        overview.setQuarterData(buildOverviewItem(quarterStart, quarterEnd));

        // 当前年度统计
        LocalDate yearStart = now.withMonth(1).withDayOfMonth(1);
        LocalDate yearEnd = now.withMonth(12).withDayOfMonth(31);
        overview.setYearData(buildOverviewItem(yearStart, yearEnd));

        // 全部数据（不限时间范围）
        overview.setAllData(buildOverviewItem(null, null));

        return overview;
    }

    /**
     * 构建指定时间范围内的统计项
     *
     * @param startDate 开始日期（null 表示不限）
     * @param endDate   结束日期（null 表示不限）
     * @return 统计项
     */
    private TaskStatusOverviewVO.TaskStatusOverviewItem buildOverviewItem(LocalDate startDate, LocalDate endDate) {
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

        TaskStatusOverviewVO.TaskStatusOverviewItem item = new TaskStatusOverviewVO.TaskStatusOverviewItem();
        item.setTotalTaskCount(tasks.size());
        item.setPendingTaskCount(pendingCount);
        item.setOngoingTaskCount(ongoingCount);
        item.setReviewTaskCount(reviewCount);
        item.setRejectedTaskCount(rejectedCount);
        item.setCompletedTaskCount(completedCount);

        return item;
    }
}