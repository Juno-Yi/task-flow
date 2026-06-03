import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目结项列表（分页）
 */
export function fetchGetProjectAacceptanceList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/acceptance/list',
        params
    })
}

/**
 * 项目通过验收
 * @param projectId 项目ID
 */
export function fetchPassAcceptance(projectId: number){
    return request.post<void>({
        url: `/project/acceptance/${projectId}/pass`
    })
}

/**
 * 项目验收驳回
 * @param projectId 项目ID
 */
export function fetchRejectAcceptance(projectId: number){
    return request.post<void>({
        url: `/project/acceptance/${projectId}/reject`
    })
}
