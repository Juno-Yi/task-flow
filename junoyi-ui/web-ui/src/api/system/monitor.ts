import request from '@/utils/http'

/**
 * 获取系统监控信息
 */
export function fetchGetSystemMonitor() {
  return request.get<Api.System.SystemMonitorVO>({
    url: '/system/info/monitor'
  })
}
