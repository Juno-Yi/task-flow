import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取已归档项目列表（分页）
 */
export function fetchGetProjectArchivedList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/archived/list',
        params
    })
}

