/**
 * API 项目空间
 */
declare namespace Api.Project {

    /**
     * 项目里程碑 VO
     */
    interface ProjectMilestoneVO {
        id: number
        name: string
        description: string
        status: number
        statusLabel: string
        statusType: string
        dueTime: string
        finishTime: string
        sort: number
        ownerId: number
        nickName: string
        createTime: string
        updateTime: string
    }

    /**
     * 项目里程碑 DTO
     */
    interface ProjectMilestoneDTO {
        id?: number
        projectId: number
        name: string
        description: string
        dueTime?: string
        sort: number
        ownerId?: number
    }

}