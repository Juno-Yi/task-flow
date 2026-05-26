import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目动态记录列表（分页）
 */
export function fetchGetProjectRecordList(params: Api.Project.ProjectRecordQueryDTO) {
  return request.get<PageResult<Api.Project.ProjectRecordVO>>({
    url: '/project/record/list',
    params
  })
}

