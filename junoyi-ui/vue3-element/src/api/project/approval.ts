import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取立项项目列表（分页）
 */
export function fetchGetProjectApprovalList(params: Api.Project.ProjectListQueryDTO) {
    return request.get<PageResult<Api.Project.ProjectListVO>>({
        url: '/project/approval/list',
        params
    })
}

