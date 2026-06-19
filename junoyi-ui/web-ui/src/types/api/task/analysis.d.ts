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

    interface TaskAnalysisVO {
        statusOverview: TaskStatusOverviewVO
    }
}