/**
 * 文件工具函数
 * 
 * 处理文件 URL 的拼接和预览
 */

/**
 * 获取完整的文件 URL
 * @param filePath 文件路径（可能是相对路径或完整 URL）
 * @returns 完整的文件 URL
 */
export function getFileUrl(filePath?: string | null): string {
  // 如果没有文件路径，返回空字符串
  if (!filePath) {
    return ''
  }

  // 如果已经是完整的 URL（http:// 或 https://），直接返回
  if (filePath.startsWith('http://') || filePath.startsWith('https://')) {
    return filePath
  }

  // 如果是 data URL（base64），直接返回
  if (filePath.startsWith('data:')) {
    return filePath
  }

  // 如果是相对路径，拼接 API 基础地址
  const apiUrl = import.meta.env.VITE_API_URL || ''
  
  // 确保路径以 / 开头
  const path = filePath.startsWith('/') ? filePath : `/${filePath}`
  
  // 拼接完整 URL
  return `${apiUrl}${path}`
}

/**
 * 获取图片预览 URL
 * @param imagePath 图片路径
 * @returns 完整的图片 URL
 */
export function getImageUrl(imagePath?: string | null): string {
  return getFileUrl(imagePath)
}
