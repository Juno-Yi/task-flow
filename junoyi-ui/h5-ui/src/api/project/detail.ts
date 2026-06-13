import request from '@/utils/request';

/**
 * 获取项目信息（通过项目ID）
 */
export function fetchGetProjectInfo(projectId: number) {
  return request.get<Api.Project.ProjectInfoVO>({
    url: `/project/detail/info/${projectId}`
  });
}

