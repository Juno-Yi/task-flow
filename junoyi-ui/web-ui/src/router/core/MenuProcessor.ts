/**
 * 菜单处理器
 *
 * 负责从后端获取菜单数据并进行处理
 *
 * @module router/core/MenuProcessor
 * @author JunoYi Team
 */

import type { AppRouteRecord } from '@/types/router';
import { RoutesAlias } from '../routesAlias';
import { formatMenuTitle } from '@/utils';
import { fetchGetRouter } from "@/api/system/router";

export class MenuProcessor {
  /**
   * 获取菜单数据（从后端接口获取）
   */
  async getMenuList(): Promise<AppRouteRecord[]> {
    const menuList = await fetchGetRouter();

    // 在规范化路径之前，验证原始路径配置
    this.validateMenuPaths(menuList)

    // 规范化路径（将相对路径转换为完整路径）
    return this.normalizeMenuPaths(menuList)
  }

  /**
   * 递归过滤空菜单项
   */
  private filterEmptyMenus(menuList: AppRouteRecord[]): AppRouteRecord[] {
    return menuList
      .map((item) => {
        if (item.children && item.children.length > 0) {
          const filteredChildren = this.filterEmptyMenus(item.children)
          return { ...item, children: filteredChildren }
        }
        return item
      })
      .filter((item) => {
        if ('children' in item) return true
        if (item.meta?.isIframe === true || item.meta?.link) return true
        if (item.component && item.component !== '' && item.component !== RoutesAlias.Layout) {
          return true
        }
        return false
      })
  }

  /**
   * 验证菜单列表是否有效
   */
  validateMenuList(menuList: AppRouteRecord[]): boolean {
    return Array.isArray(menuList) && menuList.length > 0
  }

  /**
   * 规范化菜单路径
   */
  private normalizeMenuPaths(menuList: AppRouteRecord[], parentPath = ''): AppRouteRecord[] {
    return menuList.map((item) => {
      const fullPath = this.buildFullPath(item.path || '', parentPath)
      const children = item.children?.length
        ? this.normalizeMenuPaths(item.children, fullPath)
        : item.children

      return { ...item, path: fullPath, children }
    })
  }

  /**
   * 验证菜单路径配置
   */
  private validateMenuPaths(menuList: AppRouteRecord[], level = 1): void {
    menuList.forEach((route) => {
      if (!route.children?.length) return

      const parentName = String(route.name || route.path || '未知路由')

      route.children.forEach((child) => {
        const childPath = child.path || ''
        if (this.isValidAbsolutePath(childPath)) return
        if (childPath.startsWith('/')) {
          this.logPathError(child, childPath, parentName, level)
        }
      })

      this.validateMenuPaths(route.children, level + 1)
    })
  }

  /**
   * 判断是否为合法的绝对路径
   */
  private isValidAbsolutePath(path: string): boolean {
    return (
      path.startsWith('http://') ||
      path.startsWith('https://') ||
      path.startsWith('/outside/iframe/')
    )
  }

  /**
   * 输出路径配置错误日志
   */
  private logPathError(
    route: AppRouteRecord,
    path: string,
    parentName: string,
    level: number
  ): void {
    const routeName = String(route.name || path || '未知路由')
    const menuTitle = route.meta?.title || routeName
    const suggestedPath = path.split('/').pop() || path.slice(1)

    console.error(
      `[路由配置错误] 菜单 "${formatMenuTitle(menuTitle)}" (name: ${routeName}, path: ${path}) 配置错误\n` +
        `  位置: ${parentName} > ${routeName}\n` +
        `  问题: ${level + 1}级菜单的 path 不能以 / 开头\n` +
        `  当前配置: path: '${path}'\n` +
        `  应该改为: path: '${suggestedPath}'`
    )
  }

  /**
   * 构建完整路径
   */
  private buildFullPath(path: string, parentPath: string): string {
    if (!path) return ''
    if (path.startsWith('http://') || path.startsWith('https://')) return path
    if (path.startsWith('/')) return path

    if (parentPath) {
      const cleanParent = parentPath.replace(/\/$/, '')
      const cleanChild = path.replace(/^\//, '')
      return `${cleanParent}/${cleanChild}`
    }

    return `/${path}`
  }
}
