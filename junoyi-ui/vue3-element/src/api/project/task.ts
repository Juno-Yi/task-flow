import request from '@/utils/http'

/**
 * 获取项目任务列表
 * @param projectId 项目ID
 */
export function fetchGetProjectTaskList(projectId: number){
    return request.get<Api.Project.ProjectTaskItemVO[]>({
        url: `/project/task/list/${projectId}`
    })
}