declare namespace Api.Project {

    /**
     * 项目任务 VO 数据对象
     */
    interface ProjectTaskItemVO {
        id: number
        title: string
        description: string
        status: number
        priority: number
        ownerUser?: OwnerUser
        taskUserList?: TaskUser[]
        planStartTime?: string
        planEndTime?: string
        isOverdue?: boolean
        remark?: string
    }

    /**
     * 项目任务创建 DTO
     */
    interface ProjectTaskCreatedDTO {
        projectId: number
        title: string
        description?: string
        priority?: number
        planStartTime?: string
        planEndTime?: string
        userIds?: number[]
        ownerUserId?: number
        remark?: string
    }

    /**
     * 项目任务更新 DTO
     */
    interface ProjectTaskUpdateDTO {
        id: number
        title?: string
        description?: string
        priority?: number
        planStartTime?: string
        planEndTime?: string
        userIds?: number[]
        ownerUserId?: number
        remark?: string
    }
}