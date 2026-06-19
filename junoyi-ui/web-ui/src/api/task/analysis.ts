import request from '@/utils/http'

/**
 * 获取任务分析综合数据
 */
export function fetchGetTaskAnalysis(){
    return request.get<Api.Task.TaskAnalysisVO>({
        url: '/task/analysis'
    })
}