import request from '@/utils/http'
import { PageResult } from '@/types'

/**
 * 获取项目列表（分页）
 */
export function fetchGetRepoList(params: Api.Project.ProjectListQueryDTO) {
  return request.get<PageResult<Api.Project.ProjectListVO>>({
    url: '/project/list/list',
    params
  })
}

/**
 * 获取项目下拉列表
 */
export function fetchGetProjectOptions(params: Api.Project.ProjectOptionQueryDTO){
  return request.get<Api.Project.ProjectOptionVO[]>({
    url: '/project/list/options',
    params
  })
}

/**
 * 添加项目
 */
export function fetchAddRepo(data: Api.Project.ProjectListDTO) {
  return request.post<void>({
    url: '/project/list',
    data,
  })
}

/**
 * 更新项目
 */
export function fetchUpdateRepo(data: Api.Project.ProjectListDTO) {
  return request.put<void>({
    url: '/project/list',
    data,
  })
}

/**
 * 删除项目（需要密码验证）
 */
export function fetchDeleteRepo(id: number, data: { password: string }) {
  return request.post<void>({
    url: `/project/list/delete/${id}`,
    data,
    showSuccessMessage: true,
    showErrorMessage: true
  })
}

/**
 * 批量删除项目（需要密码验证）
 */
export function fetchDeleteRepoBatch(data: Api.Project.ProjectDeleteDTO) {
  return request.post<void>({
    url: '/project/list/delete/batch',
    data,
    showSuccessMessage: true,
    showErrorMessage: true
  })
}

/**
 * 导出项目书
 */
export function fetchExportProjectBook(ids: number[]) {
  return request.post<Blob>({
    url: '/project/list/export',
    data: ids,
    responseType: 'blob',
    showSuccessMessage: true
  })
}

/**
 * 同步项目
 */
export function fetchSyncRepo(id: number) {
  return request.post<void>({
    url: `/project/${id}/sync`,
    showSuccessMessage: true
  })
}

/**
 * 获取项目详情（通过ID）
 */
export function fetchGetRepoDetail(id: number) {
  return request.get<Api.Project.ProjectDetailVO>({
    url: `/project/${id}`
  })
}
