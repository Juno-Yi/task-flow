import request from '@/utils/request';
import type { PageResult } from '@/types';

/**
 * 我的任务列表查询参数
 */
export interface MyTaskListParams {
  /** 任务状态 */
  status: number;
  /** 当前页码 */
  current?: number;
  /** 每页数量 */
  size?: number;
}

/**
 * 获取我的任务列表（分页）
 * @param params 查询参数
 * @returns 任务列表（分页）
 */
export function fetchGetMyTaskList(params: MyTaskListParams) {
  return request.get<PageResult<Api.Task.TaskItemVO>>({
    url: `/task/my-task/list/${params.status}`,
    params: {
      current: params.current,
      size: params.size
    }
  });
}


/**
 * 获取我的任务详情
 */
export function fetchGetMyTaskDetail(taskId: number) {
  return request.get<Api.Task.TaskListDetailVO>({
    url: `/task/my-task/${taskId}`
  });
}

/**
 * 开始任务
 */
export function fetchStartMyTask(taskId: number) {
  return request.put<void>({
    url: `/task/my-task/start/${taskId}`
  });
}

/**
 * 提交任务
 */
export function fetchSubmitMyTask(data: Api.Task.TaskSubmitDTO) {
  return request.post<void>({
    url: '/task/my-task/commit',
    data
  });
}