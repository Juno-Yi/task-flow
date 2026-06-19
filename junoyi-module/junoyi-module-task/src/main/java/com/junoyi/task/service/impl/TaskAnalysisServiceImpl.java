package com.junoyi.task.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.task.domain.po.Task;
import com.junoyi.task.domain.vo.HealthScoreVO;
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
        vo.setHealthScore(buildHealthScore());
        return vo;
    }

    /**
     * 构建任务健康分（按维度）
     * 任务健康分 = 完成率 × 50% + (100 - 逾期率) × 30% + (100 - 驳回率) × 20%
     */
    private HealthScoreVO buildHealthScore() {
        HealthScoreVO score = new HealthScoreVO();

        LocalDate now = LocalDate.now();

        // 当前月
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        score.setMonthData(calculateHealthPoint(monthStart, monthEnd));

        // 当前季度
        int quarterStartMonth = (now.getMonthValue() - 1) / 3 * 3 + 1;
        LocalDate quarterStart = now.withMonth(quarterStartMonth).withDayOfMonth(1);
        LocalDate quarterEnd = quarterStart.plusMonths(2);
        quarterEnd = quarterEnd.withDayOfMonth(quarterEnd.lengthOfMonth());
        score.setQuarterData(calculateHealthPoint(quarterStart, quarterEnd));

        // 当前年度
        LocalDate yearStart = now.withMonth(1).withDayOfMonth(1);
        LocalDate yearEnd = now.withMonth(12).withDayOfMonth(31);
        score.setYearData(calculateHealthPoint(yearStart, yearEnd));

        // 全部
        score.setAllData(calculateHealthPoint(null, null));

        return score;
    }

    /**
     * 计算指定时间范围的健康分
     * 公式：完成率 × 50% + (100 - 逾期率) × 30% + (100 - 驳回率) × 20%
     */
    private Double calculateHealthPoint(LocalDate startDate, LocalDate endDate) {
        Date start = null;
        Date end = null;

        if (startDate != null) {
            start = Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        }
        if (endDate != null) {
            end = Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        }

        List<Task> tasks = taskMapper.selectList(
                new LambdaQueryWrapper<Task>()
                        .eq(Task::getDelFlag, false)
                        .ge(start != null, Task::getCreateTime, start)
                        .le(end != null, Task::getCreateTime, end)
        );

        int totalCount = tasks.size();
        if (totalCount == 0) {
            return 100.0;
        }

        int completedCount = 0;
        int overdueCount = 0;
        int rejectedCount = 0;
        Date nowDate = new Date();

        for (Task task : tasks) {
            if (Integer.valueOf(4).equals(task.getStatus())) {
                completedCount++;
            }
            if (Integer.valueOf(3).equals(task.getStatus())) {
                rejectedCount++;
            }
            // 逾期判断：
            // 1. 没有计划结束时间：不算逾期
            // 2. 已完成：实际结束时间超过计划结束时间算逾期
            // 3. 未完成：当前时间超过计划结束时间算逾期
            if (task.getPlanEndTime() != null) {
                if (Integer.valueOf(4).equals(task.getStatus())) {
                    if (task.getEndTime() != null && task.getEndTime().after(task.getPlanEndTime())) {
                        overdueCount++;
                    }
                } else if (nowDate.after(task.getPlanEndTime())) {
                    overdueCount++;
                }
            }
        }

        // 完成率（0-100）
        double completionRate = (double) completedCount / totalCount * 100;
        // 逾期率（0-100）
        double overdueRate = (double) overdueCount / totalCount * 100;
        // 驳回率（0-100）
        double rejectedRate = (double) rejectedCount / totalCount * 100;

        // 健康分 = 完成率 × 50% + (100 - 逾期率) × 30% + (100 - 驳回率) × 20%
        double healthPoint = completionRate * 0.5
                + (100 - overdueRate) * 0.3
                + (100 - rejectedRate) * 0.2;

        return Math.round(healthPoint * 10) / 10.0;
    }

    /**
     * 构建核心KPI数据（按维度）
     */
    private TaskCoreKpiVO buildCoreKpi() {
        TaskCoreKpiVO kpi = new TaskCoreKpiVO();

        LocalDate now = LocalDate.now();

        // 当前月
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd = now.withDayOfMonth(now.lengthOfMonth());
        kpi.setMonthData(buildKpiItem(monthStart, monthEnd));

        // 当前季度
        int quarterStartMonth = (now.getMonthValue() - 1) / 3 * 3 + 1;
        LocalDate quarterStart = now.withMonth(quarterStartMonth).withDayOfMonth(1);
        LocalDate quarterEnd = quarterStart.plusMonths(2);
        quarterEnd = quarterEnd.withDayOfMonth(quarterEnd.lengthOfMonth());
        kpi.setQuarterData(buildKpiItem(quarterStart, quarterEnd));

        // 当前年度
        LocalDate yearStart = now.withMonth(1).withDayOfMonth(1);
        LocalDate yearEnd = now.withMonth(12).withDayOfMonth(31);
        kpi.setYearData(buildKpiItem(yearStart, yearEnd));

        // 全部
        kpi.setAllData(buildKpiItem(null, null));

        return kpi;
    }

    /**
     * 构建指定时间范围内的KPI项
     */
    private TaskCoreKpiVO.TaskCoreKpiItem buildKpiItem(LocalDate startDate, LocalDate endDate) {
        final Date start = startDate != null
                ? Date.from(startDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant())
                : null;
        final Date end = endDate != null
                ? Date.from(endDate.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant())
                : null;

        // 查询与时间范围有关的所有未删除任务：
        // 1. 创建时间在范围内（本期新增）
        // 2. 计划结束时间在范围内
        // 3. 实际结束时间在范围内（已完成但可能逾期）
        // 4. 未完成且计划结束时间在范围结束之前（仍在逾期中）
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<Task>()
                .eq(Task::getDelFlag, false)
                .and(start != null || end != null, w -> w
                        // 创建时间在范围内
                        .and(inner -> inner
                                .ge(start != null, Task::getCreateTime, start)
                                .le(end != null, Task::getCreateTime, end)
                        )
                        // 或者计划结束时间在范围内
                        .or(inner -> inner
                                .ge(start != null, Task::getPlanEndTime, start)
                                .le(end != null, Task::getPlanEndTime, end)
                        )
                        // 或者实际结束时间在范围内（已完成的任务，判断是否逾期完成）
                        .or(inner -> inner
                                .ge(start != null, Task::getEndTime, start)
                                .le(end != null, Task::getEndTime, end)
                        )
                        // 或者未完成且计划结束时间在范围结束之前（持续逾期中）
                        .or(end != null, inner -> inner
                                .ne(Task::getStatus, 4)
                                .le(Task::getPlanEndTime, end)
                                .isNotNull(Task::getPlanEndTime)
                        )
                );

        List<Task> tasks = taskMapper.selectList(wrapper);

        // 去重（同一个任务可能匹配多个条件）
        tasks = tasks.stream()
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(() -> new java.util.TreeSet<>(java.util.Comparator.comparing(Task::getId))),
                        java.util.ArrayList::new
                ));

        // 统计本期新增任务数（仅按 createTime 在范围内）
        int newTaskCount = (int) tasks.stream()
                .filter(t -> {
                    if (t.getCreateTime() == null) return false;
                    if (start != null && t.getCreateTime().before(start)) return false;
                    if (end != null && t.getCreateTime().after(end)) return false;
                    return true;
                })
                .count();

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

            // 逾期判断：
            // 1. 没有计划结束时间：不算逾期
            // 2. 已完成：实际结束时间超过计划结束时间算逾期
            // 3. 未完成：当前时间超过计划结束时间算逾期
            if (task.getPlanEndTime() != null) {
                if (Integer.valueOf(4).equals(task.getStatus())) {
                    if (task.getEndTime() != null && task.getEndTime().after(task.getPlanEndTime())) {
                        overdueCount++;
                    }
                } else if (nowDate.after(task.getPlanEndTime())) {
                    overdueCount++;
                }
            }
        }

        TaskCoreKpiVO.TaskCoreKpiItem item = new TaskCoreKpiVO.TaskCoreKpiItem();

        // 完成率
        double completionRate = totalCount > 0 ? (double) completedCount / totalCount * 100 : 0;
        item.setCompletionRate(Math.round(completionRate * 10) / 10.0);

        // 逾期任务数
        item.setOverdueTaskCount(overdueCount);

        // 平均处理时长（小时）
        double avgHours = processedCount > 0
                ? (double) totalProcessMillis / processedCount / TimeUnit.HOURS.toMillis(1)
                : 0;
        item.setAvgProcessHours(Math.round(avgHours * 10) / 10.0);

        // 本期新增任务数
        item.setNewTaskCount(newTaskCount);

        return item;
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