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

