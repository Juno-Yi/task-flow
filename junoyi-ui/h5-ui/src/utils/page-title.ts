import type { RouteLocationNormalized } from 'vue-router';
import { i18n } from '@/locales';

/**
 * 获取系统默认标题
 */
export function getDefaultTitle(): string {
  return import.meta.env.VITE_APP_TITLE || '任务管理系统';
}

/**
 * 设置页面标题
 * @param route 路由对象
 */
export function setPageTitle(route: RouteLocationNormalized) {
  const { title } = route.meta;
  
  if (!title) {
    document.title = getDefaultTitle();
    return;
  }

  // 如果 title 是 i18n key（如 'common.tabbar.home'），则翻译
  // 否则直接使用 title
  let pageTitle: string;
  
  if (typeof title === 'string' && title.includes('.')) {
    // 尝试从 i18n 获取翻译
    const t = i18n.global.t;
    const translated = t(title);
    
    // 如果翻译结果和 key 相同，说明没有找到翻译，直接使用 title
    pageTitle = translated === title ? title : translated;
  } else {
    pageTitle = String(title);
  }

  // 设置页面标题
  document.title = pageTitle;
}

/**
 * 重置页面标题为默认值
 */
export function resetPageTitle() {
  document.title = getDefaultTitle();
}

