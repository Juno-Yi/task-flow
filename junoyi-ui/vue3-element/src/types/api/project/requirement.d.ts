declare namespace Api.Project {

    /**
     * 需求 VO
     */
    interface ProjectRequirementVO {
        id: number
        projectId: number
        requirementNo: string
        title: string
        description: string
        priority: number
        priorityLabel: string
        priorityType: string
        status: number
        statusLabel: string
        statusType: string
        source: number
        sourceLabel: string
        sourceType: string
        type: number
        typeLabel: string
        typeLabelType: string
        createTime: string
        updateTime: string
    }


    /**
     * 需求查询参数 DTO
     */
    interface ProjectRequirementQueryDTO {
        title?: string
        priority?: number
        status?: number
        source?: number
        type?: number
    }

    /**
     * 需求添加修改表单 DTO
     */
    interface ProjectRequirementDTO {

    }
}