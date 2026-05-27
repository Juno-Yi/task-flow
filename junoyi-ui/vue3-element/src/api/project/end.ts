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

