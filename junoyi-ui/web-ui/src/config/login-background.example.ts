/**
 * 登录背景配置示例
 * 
 * 这是一个示例文件，展示如何配置登录背景图片
 * 
 * 使用方法：
 * 1. 复制这个文件的内容到 src/config/login-background.ts
 * 2. 或者直接修改 src/config/login-background.ts 文件
 */

// 示例 1: 使用项目中已有的背景图片
import bgImage from '@imgs/login/bg.png'

export const loginBackgrounds = {
  center: bgImage,      // 居中布局使用 bg.png
  leftRight: bgImage,   // 左右布局使用 bg.png
  rightLeft: bgImage    // 右左布局使用 bg.png
}

// 示例 2: 为不同布局使用不同的背景图片
// import bgCenter from '@imgs/login/bg-center.jpg'
// import bgLeft from '@imgs/login/bg-left.jpg'
// import bgRight from '@imgs/login/bg-right.jpg'
// 
// export const loginBackgrounds = {
//   center: bgCenter,
//   leftRight: bgLeft,
//   rightLeft: bgRight
// }

// 示例 3: 只为某个布局设置背景，其他使用默认几何装饰
// import bgCenter from '@imgs/login/bg-center.jpg'
// 
// export const loginBackgrounds = {
//   center: bgCenter,    // 居中布局使用图片背景
//   leftRight: '',       // 左右布局使用默认几何装饰
//   rightLeft: ''        // 右左布局使用默认几何装饰
// }

// 示例 4: 使用网络图片
// export const loginBackgrounds = {
//   center: 'https://images.unsplash.com/photo-1557683316-973673baf926',
//   leftRight: '',
//   rightLeft: ''
// }

