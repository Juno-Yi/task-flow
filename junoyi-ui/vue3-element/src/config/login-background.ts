/**
 * 登录背景配置
 *
 * 在这里统一配置登录页面的背景图片
 *
 * 使用方法：
 * 1. 将你的背景图片放到 src/assets/images/login/ 目录下
 * 2. 在下面的配置中引入图片并设置对应的背景
 * 3. 如果不想使用背景图片，将对应的值设置为空字符串 ''
 *
 * @example
 * // 引入图片
 * import bgCenter from '@imgs/login/bg-center.jpg'
 * import bgLeft from '@imgs/login/bg-left.jpg'
 * import bgRight from '@imgs/login/bg-right.jpg'
 *
 * // 配置背景
 * export const loginBackgrounds = {
 *   center: bgCenter,
 *   leftRight: bgLeft,
 *   rightLeft: bgRight
 * }
 */

// 引入背景图片
import bgImage from '@imgs/login/bg.png'

/**
 * 登录背景配置对象
 */
export const loginBackgrounds = {
  /**
   * 居中布局背景图片
   * 用于 center 布局模式
   */
  center: bgImage,

  /**
   * 左右布局背景图片
   * 用于 left-right 布局模式（左侧背景）
   */
  leftRight: bgImage,

  /**
   * 右左布局背景图片
   * 用于 right-left 布局模式（右侧背景）
   */
  rightLeft: bgImage
}

/**
 * 获取登录背景图片
 * @param layout 布局类型
 * @returns 背景图片路径
 */
export function getLoginBackground(layout: 'center' | 'left-right' | 'right-left'): string {
  const layoutMap = {
    'center': loginBackgrounds.center,
    'left-right': loginBackgrounds.leftRight,
    'right-left': loginBackgrounds.rightLeft
  }
  
  return layoutMap[layout] || ''
}

