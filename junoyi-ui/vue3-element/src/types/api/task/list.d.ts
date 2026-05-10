/**
 * 任务模块相关类型定义
 */
declare namespace Api.Task {
  interface TaskListQueryDTO {
    title?: string
    status?: number
    priority?: number
    userId?: number
    startTime?: string
    endTime?: string
    current?: number
    size?: number
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

  interface TaskListVO {
    id: number
    title: string
    status: number
    statusLabel?: string
    statusType?: string
    priority: number
    priorityLabel?: string
    priorityType?: string
    taskUserList?: TaskUser[]
    ownerUser?: OwnerUser
    startTime?: string
    dueTime?: string
    DueTime?: string
    finishTime?: string
    isOverdue?: boolean
    latestSubmitTime?: string
  }

  interface TaskRecordAttachment {
    id?: number
    taskId?: number
    recordId?: number
    fileName?: string
    fileUrl?: string
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

  interface TaskListDetailVO {
    id: number
    title: string
    description?: string
    status?: number
    priority: number
    taskUserList?: TaskUser[]
    ownerUser?: OwnerUser
    startTime?: string
    dueTime?: string
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

  interface TaskItemVO {
    id: number
    title: string
    description?: string
    status: number
    priority: number
    ownerUser?: OwnerUser
    taskUserList?: TaskUser[]
    startTime?: string
    dueTime?: string
    DueTime?: string
    isOverdue?: boolean
    remark?: string
    projectId?: number
  }

  interface TaskFormData {
    id?: number
    title: string
    description?: string
    priority: number
    ownerUserId?: number
    userIds: number[]
    dueTime?: string
    remark?: string
    syncSchedule?: boolean
  }

  interface TaskListDTO {
    id?: number
    title: string
    description?: string
    priority?: number
    dueTime?: string
    userIds?: number[]
    ownerUserId?: number
    remark?: string
    syncSchedule?: boolean
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

  interface TaskApprovalDTO {
    taskId: number
    remark?: string
  }

  interface TaskLogQueryDTO {
    taskTitle?: string
    operatorId?: number
    actionType?: number
    current?: number
    size?: number
  }

  interface TaskLogVO {
    id: number
    taskId: number
    taskTitle?: string
    operatorId?: number
    operatorNickName?: string
    actionType?: number
    actionTypeLabel?: string
    remark?: string
    createTime?: string
  }
}

