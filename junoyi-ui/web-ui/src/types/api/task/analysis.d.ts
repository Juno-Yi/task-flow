declare namespace Api.Task {


    interface TaskAnalysisOverviewItem {
        pendingTaskCount: number
        ongoingTaskCount: number
        reviewTaskCount: number
        rejectedTaskCount: number
        completedTaskCount: number
    }

    interface TaskAnalysisOverviewVO {
        monthData: TaskAnalysisOverviewItem
        quarterData: TaskAnalysisOverviewItem
        yearData: TaskAnalysisOverviewItem
        allData: TaskAnalysisOverviewItem
    }
}