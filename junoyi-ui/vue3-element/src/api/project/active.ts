import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目列表（分页）
 */
export function fetchGetProjectActiveList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/active/list',
        params
    })
}

