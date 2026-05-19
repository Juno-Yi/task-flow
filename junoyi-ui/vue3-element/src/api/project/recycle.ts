import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目回收站列表（分页）
 */
export function fetchGetProjectRecycleList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/recycle/list',
        params
    })
}

/**
 * 恢复已删除的项目
 * @param projectId 项目ID
 */
export function fetchRestoreProject(projectId: number){
    return request.post<void>({
        url: `/project/recycle/${projectId}/restore`,
    })
}

/**
 * 彻底删除项目
 * @param projectId 项目ID
 */
export function fetchDeleteProject(projectId: number){
    return request.post<void>({
        url: `/project/recycle/${projectId}/delete`
    })
}