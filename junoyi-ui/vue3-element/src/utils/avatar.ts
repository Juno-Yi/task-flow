/**
 * 头像工具函数
 * 
 * 处理用户头像 URL 的拼接和默认头像
 */

// 使用 new URL 来正确导入静态资源
const getDefaultAvatarUrl = () => {
  return new URL('@/assets/images/user/avatar.webp', import.meta.url).href
}

/**
 * 获取完整的头像 URL
 * @param avatar 用户头像路径（可能是相对路径或完整 URL）
 * @returns 完整的头像 URL 或默认头像
 */
export function getAvatarUrl(avatar?: string | null): string {
  // 如果没有头像，返回前端默认头像
  if (!avatar) {
    return getDefaultAvatarUrl()
  }

  // 如果是后端的默认头像路径，使用前端默认头像
  if (avatar === '/default-avatar.png' || avatar === 'default-avatar.png') {
    return getDefaultAvatarUrl()
  }

  // 如果已经是完整的 URL（http:// 或 https://），直接返回
  if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
    return avatar
  }

  // 如果是 data URL（base64），直接返回
  if (avatar.startsWith('data:')) {
    return avatar
  }

  // 如果是相对路径，拼接 API 基础地址
  const apiUrl = import.meta.env.VITE_API_URL || ''
  
  // 确保路径以 / 开头
  const path = avatar.startsWith('/') ? avatar : `/${avatar}`
  
  // 拼接完整 URL
  return `${apiUrl}${path}`
}

/**
 * 获取默认头像
 * @returns 默认头像路径
 */
export function getDefaultAvatar(): string {
  return getDefaultAvatarUrl()
}
