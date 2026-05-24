import request from '@/utils/http'

/**
 * 获取项目需求列表
 */
export function fetchGetProjectRequirementList(projectId: number,params: Api.Project.ProjectRequirementQueryDTO){
    return request.get<Api.Project.ProjectRequirementVO[]>({
        url: `/project/requirement/list/${projectId}`,
        params
    })
}