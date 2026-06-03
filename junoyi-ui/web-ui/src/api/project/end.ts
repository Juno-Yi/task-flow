import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目结后列表（分页）
 */
export function fetchGetProjectEndList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/end/list',
        params
    })
}

/**
 * 项目结束后归档请求
 * @param projectId 项目ID
 */
export function fetchArchiveProject(projectId: number){
    return request.post<void>({
        url: `/project/end/${projectId}/archive`
    })
}