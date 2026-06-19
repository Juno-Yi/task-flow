declare namespace Api.Task {

    interface TaskStatusOverviewItem {
        totalTaskCount: number
        pendingTaskCount: number
        ongoingTaskCount: number
        reviewTaskCount: number
        rejectedTaskCount: number
        completedTaskCount: number
    }

    interface TaskStatusOverviewVO {
        monthData: TaskStatusOverviewItem
        quarterData: TaskStatusOverviewItem
        yearData: TaskStatusOverviewItem
        allData: TaskStatusOverviewItem
    }

    interface TaskCoreKpiItem {
        /** 任务完成率（百分比） */
        completionRate: number
        /** 逾期任务数 */
        overdueTaskCount: number
        /** 平均处理时长（天） */
        avgProcessDays: number
        /** 本期新增任务数 */
        newTaskCount: number
    }

    interface TaskCoreKpiVO {
        monthData: TaskCoreKpiItem
        quarterData: TaskCoreKpiItem
        yearData: TaskCoreKpiItem
        allData: TaskCoreKpiItem
    }

    interface HealthScoreVO {
        monthData: number
        quarterData: number
        yearData: number
        allData: number
    }

    interface TaskAnalysisVO {
        statusOverview: TaskStatusOverviewVO
        coreKpi: TaskCoreKpiVO
        healthScore: HealthScoreVO
    }
}