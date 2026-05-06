import request from '@/utils/http'

/** 系统信息 */
export interface SystemInfo {
  /** 系统名称 */
  name: string
  /** 版本号 */
  version: string
  /** 版权年份 */
  copyrightYear: string
  /** 版权所有者 */
  copyright: string
  /** 备案号 */
  registration: string
  /** Logo 地址 */
  logo: string
}

/**
 * 获取系统信息
 */
export function fetchGetSystemInfo() {
  return request.get<SystemInfo>({
    url: '/system/info'
  })
}
