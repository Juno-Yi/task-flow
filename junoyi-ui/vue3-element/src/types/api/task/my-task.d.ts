declare namespace Api.Task {

    /**
     * 任务物品 VO
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

}