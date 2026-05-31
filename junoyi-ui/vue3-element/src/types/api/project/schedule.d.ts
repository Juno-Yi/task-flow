declare namespace Api.Project {

    /**
     * 项目甘特图 VO
     */
    interface ProjectGanttVO {
        projectId: number
        projectNo: string
        projectTitle: string
        status: number
        statusLabel: string
        statusType: string
        type: number
        typeLabel: string
        typeLabelType: string
        priority: number
        priorityLabel: string
        priorityType: string
        leader: number
        leaderName: string
        completionRate: number
        isOverdue: boolean
        planStartTime: string
        planEndTime: string
    }

    /**
     * 项目日程甘特图查询参数 DTO
     */
    interface ProjectGanttQueryDTO {
        projectTitle?: string
        leader?: number
    }
}