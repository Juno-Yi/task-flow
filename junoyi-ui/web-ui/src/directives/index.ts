import type { App } from 'vue'
import { setupPermissionDirective } from './core/permission'
import { setupHighlightDirective } from './business/highlight'
import { setupRippleDirective } from './business/ripple'
import { setupRolesDirective } from './core/roles'

export function setupGlobDirectives(app: App) {
  setupPermissionDirective(app) // 权限指令（基于用户 permissions）
  setupRolesDirective(app) // 角色权限指令
  setupHighlightDirective(app) // 高亮指令
  setupRippleDirective(app) // 水波纹指令
}
