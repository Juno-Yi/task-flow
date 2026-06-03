import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目需求列表（分页）
 */
export function fetchGetProjectRequirementList(projectId: number, params: Api.Project.ProjectRequirementQueryDTO & { current?: number; size?: number }){
    return request.get<PageResult<Api.Project.ProjectRequirementVO>>({
        url: `/project/requirement/list/${projectId}`,
        params
    })
}

/**
 * 添加项目需求
 */
export function fetchAddProjectRequirement(projectId: number, data: Api.Project.ProjectRequirementDTO){
    return request.post<void>({
        url: `/project/requirement/${projectId}`,
        data
    })
}

/**
 * 更新项目需求
 */
export function fetchUpdateProjectRequirement(projectId: number, data: Api.Project.ProjectRequirementDTO){
    return request.put<void>({
        url: `/project/requirement/${projectId}`,
        data
    })
}

/**
 * 删除项目需求
 */
export function fetchDeleteProjectRequirement(projectId: number, requirementId: number){
    return request.del<void>({
        url: `/project/requirement/${projectId}/remove/${requirementId}`
    })
}

/**
 * 单独更新项目需求状态
 */
export function fetchUpdateProjectRequirementStatus(projectId: number, data: Api.Project.ProjectRequirementStatusUpdateDTO){
    return request.put<void>({
        url: `/project/requirement/${projectId}/status`,
        data
    })
}