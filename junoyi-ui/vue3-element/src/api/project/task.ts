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

/**
 * 创建添加项目任务
 * @param data 数据
 */
export function fetchAddProjectTask(data: Api.Project.ProjectTaskCreatedDTO){
    return request.post<void>({
        url: '/project/task',
        data
    })
}

/**
 * 修改项目任务
 * @param projectId 项目ID
 * @param data 修改的数据
 */
export function fetchUpdateProjectTask(projectId: number, data: Api.Project.ProjectTaskUpdateDTO){
    return request.put<void>({
        url: `/project/task/${projectId}`
    })
}