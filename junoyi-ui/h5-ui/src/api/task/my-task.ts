import request from '@/utils/request';

/**
 * 获取我的任务列表
 * @param status 任务状态
 * @returns 任务列表
 */
export function fetchGetMyTaskList(status: number) {
  return request.get<Api.Task.TaskItemVO[]>({
    url: `/task/my-task/list/${status}`
  });
}