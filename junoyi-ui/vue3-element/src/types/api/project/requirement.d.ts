declare namespace Api.Project {

    interface ProjectRequirementVO {

    }


    /**
     * 需求查询参数 DTO
     */
    interface ProjectRequirementQueryDTO {
        title: string
        priority: number
        status: number
        source: number
        type: number
    }
}