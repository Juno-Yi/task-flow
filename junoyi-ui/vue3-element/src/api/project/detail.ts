import request from '@/utils/http'

/**
 * 获取项目详情（通过项目编号）
 * @param no
 */
export function fetchGetProjectDetailByNo(no: string){
    return request.get<Api.Project.ProjectDetailVO>({
        url: `/project/detail/${no}`
    })
}

/**
 * 获取项目概览数据
 */
export function fetchGetProjectOverview(no: string){
    return request.get<Api.Project.ProjectOverviewVO>({
        url: `/project/detail/${no}/overview`
    })
}