import request from '@/utils/http'

/**
 * 获取项目日程甘特图列表
 */
export function fetchGetProjectScheduleGantList(params: Api.Project.ProjectGanttQueryDTO){
    return request.get<Api.Project.ProjectGanttVO[]>({
        url: '/project/schedule/gantt/list',
        params
    })
}