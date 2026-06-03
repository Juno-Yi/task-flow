import request from '@/utils/http'
import type { PageResult } from '@/types/common/response'
import ConfigVO = Api.System.ConfigVO;

/**
 * 获取系统参数列表（分页）
 */
export function fetchGetConfigList(params?: Api.System.ConfigQueryDTO) {
  return request.get<PageResult<Api.System.ConfigVO>>({
    url: '/system/config/list',
    params
  })
}

/**
 * 获取系统参数详情
 */
export function fetchGetConfigById(id: number | string) {
  return request.get<Api.System.ConfigVO>({
    url: `/system/config/${id}`
  })
}

/**
 * 根据参数键名获取参数值
 */
export function fetchGetConfigByKey(configKey: string) {
  return request.get<string>({
    url: `/system/config/key/${configKey}`
  })
}

/**
 * 添加系统参数
 */
export function fetchAddConfig(data: Api.System.ConfigDTO) {
  return request.post<void>({
    url: '/system/config',
    data,
  })
}

/**
 * 更新系统参数
 */
export function fetchUpdateConfig(data: Api.System.ConfigDTO) {
  return request.put<void>({
    url: '/system/config',
    data,
  })
}

/**
 * 删除系统参数
 */
export function fetchDeleteConfig(id: number | string) {
  return request.del<void>({
    url: `/system/config/${id}`,
  })
}

/**
 * 批量删除系统参数
 */
export function fetchDeleteConfigBatch(ids: number[]) {
  return request.del<void>({
    url: '/system/config/batch',
    data: ids,
  })
}

/**
 * 刷新系统参数缓存
 */
export function fetchRefreshConfigCache() {
  return request.post<void>({
    url: '/system/config/refresh',
  })
}

/**
 * 获取系统应用配置
 * 用于获取系统级别的应用配置（需要登录后调用）
 * 包括：水印配置、主题配置、功能开关等
 */
export function fetchGetSystemAppConfig() {
  return request.get<Api.System.ConfigVO[]>({
    url: '/system/config/app'
  })
}
