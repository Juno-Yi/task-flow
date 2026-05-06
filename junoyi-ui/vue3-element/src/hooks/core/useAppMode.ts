/**
 * useAppMode - 应用模式管理
 *
 * 当前系统固定使用后端模式，权限由后端接口返回的菜单数据控制
 *
 * @module useAppMode
 * @author JunoYi Team
 */

import { computed } from 'vue'

export function useAppMode() {
  /**
   * 是否为前端控制模式（已废弃，始终返回 false）
   */
  const isFrontendMode = computed(() => false)

  /**
   * 是否为后端控制模式（始终返回 true）
   */
  const isBackendMode = computed(() => true)

  /**
   * 当前应用模式
   */
  const currentMode = computed(() => 'backend')

  return {
    isFrontendMode,
    isBackendMode,
    currentMode
  }
}
