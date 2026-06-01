import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目执行列表（分页）
 */
export function fetchGetProjectActiveList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/execution/list',
        params
    })
}

/**
 * 项目发起验收
 * @param projectId 项目Id
 */
export function fetchInitiateAcceptance(projectId: number){
    return request.post<void>({
        url: `/project/execution/initiate/acceptance/${projectId}`
    })
}

/**
 * 暂停项目
 * @param projectId 项目ID
 */
export function fetchPauseProject(projectId: number){
    return request.post<void>({
        url: `/project/execution/pause/${projectId}`
    })
}

/**
 * 取消暂停项目
 * @param projectId 项目ID
 */
export function fetchCancelPauseProject(projectId: number){
    return request.post<void>({
        url: `/project/execution/pause/cancel/${projectId}`
    })
}