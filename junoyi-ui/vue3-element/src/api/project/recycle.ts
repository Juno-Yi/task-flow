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

