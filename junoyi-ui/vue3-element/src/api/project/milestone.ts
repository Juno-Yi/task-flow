import request from '@/utils/http'

/**
 * 获取项目里程碑列表
 */
export function fetchGetProjectMilestoneList(projectId: number){
    return request.get<Api.Project.ProjectMilestoneVO[]>({
        url: `/project/milestone/list/${projectId}`
    })
}

/**
 * 添加项目里程碑
 */
export function fetchAddProjectMilestone(data: Api.Project.ProjectMilestoneDTO){
    return request.post<void>({
        url: '/project/milestone',
        data
    })
}

/**
 * 更新项目里程碑
 */
export function fetchUpdateProjectMilestone(data: Api.Project.ProjectMilestoneDTO){
    return request.put<void>({
        url: '/project/milestone',
        data
    })
}

/**
 * 删除项目里程碑
 */
export function fetchDeleteProjectMilestone(id: number){
    return request.del<void>({
        url: `/project/milestone/${id}`,
    })
}

/**
 * 完成项目里程碑
 */
export function fetchCompleteProjectMilestone(id: number){
    return request.put<void>({
        url: `/project/milestone/${id}/complete`,
    })
}