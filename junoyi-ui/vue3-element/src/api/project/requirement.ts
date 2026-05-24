import request from '@/utils/http'

/**
 * 获取项目需求列表（分页）
 */
export function fetchGetProjectRequirementList(projectId: number, params: Api.Project.ProjectRequirementQueryDTO & Api.Common.PageQuery){
    return request.get<Api.Common.PageResult<Api.Project.ProjectRequirementVO>>({
        url: `/project/requirement/list/${projectId}`,
        params
    })
}