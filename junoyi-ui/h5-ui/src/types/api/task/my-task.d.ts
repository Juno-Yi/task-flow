declare namespace Api.Task {

    /**
     * 任务物品 VO（我的任务列表项）
     */
    interface TaskItemVO {
        id: number
        title: string
        description?: string
        status: number
        priority: number
        ownerUser?: OwnerUser
        taskUserList?: TaskUser[]
        planStartTime?: string
        planEndTime?: string
        isOverdue?: boolean
        remark?: string
    }

    interface TaskUser {
        userId: number
        avatar?: string
        nickName?: string
    }

    interface OwnerUser {
        userId: number
        avatar?: string
        nickName?: string
    }


    interface TaskListDetailVO {
        id: number
        title: string
        description?: string
        status?: number
        statusLabel?: string
        statusType?: string
        priority: number
        priorityLabel?: string
        priorityType?: string
        taskUserList?: TaskUser[]
        ownerUser?: OwnerUser
        planStartTime?: string
        planEndTime?: string
        startTime?: string
        endTime?: string
        remark?: string
        isOverdue?: boolean
        projectId?: number
        createBy?: string
        createTime?: string
        updateBy?: string
        updateTime?: string
        recordList?: TaskRecordItem[]
        latestSubmitRecord?: TaskRecordItem
        latestRejectRecord?: TaskRecordItem
    }

    interface TaskRecordItem {
        id?: number
        taskId?: number
        operatorId?: number
        operatorName?: string
        operatorAvatar?: string
        actionType?: number
        actionTypeLabel?: string
        remark?: string
        createTime?: string
        attachments?: TaskRecordAttachment[]
    }

    interface TaskRecordAttachment {
        id?: number
        taskId?: number
        recordId?: number
        fileName?: string
        fileUrl?: string
    }

    interface TaskSubmitAttachment {
        fileName?: string
        fileUrl?: string
        filePath?: string
    }

    interface TaskSubmitDTO {
        taskId: number
        remark?: string
        attachments?: TaskSubmitAttachment[]
    }



}