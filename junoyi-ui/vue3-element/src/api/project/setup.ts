import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取立项项目列表（分页）
 */
export function fetchGetProjectSetupList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/setup/list',
        params
    })
}

/**
 * 启动项目
 */
export function fetchStartProject(projectId: number) {
    return request.post<void>({
        url: `/project/setup/start/${projectId}`,
    })
}
