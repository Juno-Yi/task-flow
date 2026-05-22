/**
 * 项目仓库 API
 */
import request from '@/utils/http'

/**
 * 根据项目ID获取仓库列表
 */
export function fetchGetRepoRepositoryList(projectId: number) {
  return request.get<Api.Project.ProjectRepositoryVO[]>({
    url: `/project/repository/list/${projectId}`
  })
}

/**
 * 根据ID获取仓库详情
 */
export function fetchGetRepoRepositoryById(id: number) {
  return request.get<Api.Project.ProjectRepositoryVO>({
    url: `/project/repository/${id}`
  })
}

/**
 * 添加项目仓库
 */
export function fetchAddRepoRepository(data: Api.Project.ProjectRepositoryDTO) {
  return request.post<void>({
    url: '/project/repository',
    data,
  })
}

/**
 * 更新项目仓库
 */
export function fetchUpdateRepoRepository(data: Api.Project.ProjectRepositoryDTO) {
  return request.put<void>({
    url: '/project/repository',
    data,
  })
}

/**
 * 删除项目仓库
 */
export function fetchDeleteRepoRepository(id: number) {
  return request.del<void>({
    url: `/project/repository/${id}`,
  })
}


