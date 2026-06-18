import request from '@/utils/http'

/**
 * 获取任务分析总览数据
 */
export function fetchGetTaskAnalysisOverview(){
    return request.get<Api.Task.TaskAnalysisOverviewVO>({
        url: '/task/analysis/overview'
    })
}