import request from '@/utils/http'

/**
 * 获取 Redis 服务器信息
 */
export function fetchGetRedisInfo() {
  return request.get<Api.System.RedisInfo>({
    url: '/system/cache/info'
  })
}

/**
 * 获取缓存键列表
 */
export function fetchGetCacheKeys(params: Api.System.CacheKeyQueryParams) {
  return request.get<Api.System.CacheKeyListResponse>({
    url: '/system/cache/keys',
    params
  })
}

/**
 * 获取缓存键值详情
 */
export function fetchGetCacheKeyDetail(key: string) {
  return request.get<Api.System.CacheKeyDetailVO>({
    url: '/system/cache/key',
    params: { key }
  })
}

/**
 * 删除缓存键
 */
export function fetchDeleteCacheKey(key: string) {
  return request.del<void>({
    url: `/system/cache/key?key=${encodeURIComponent(key)}`,
    showSuccessMessage: true
  })
}

/**
 * 批量删除缓存键
 */
export function fetchBatchDeleteCacheKeys(keys: string[]) {
  return request.del<void>({
    url: '/system/cache/batch',
    data: keys,
    showSuccessMessage: true
  })
}

/**
 * 清空所有缓存
 */
export function fetchClearAllCache() {
  return request.del<void>({
    url: '/system/cache/clear',
    showSuccessMessage: true
  })
}
