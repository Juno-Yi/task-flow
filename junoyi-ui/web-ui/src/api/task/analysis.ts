import request from '@/utils/http'

/**
 * 获取任务状态总览数据
 */
export function fetchGetTaskStatusOverview(){
    return request.get<Api.Task.TaskStatusOverviewVO>({
        url: '/task/analysis/status-overview'
    })
}