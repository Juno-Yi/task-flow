/**
 * 水印工具函数
 * 提供水印文本占位符替换功能
 */

/**
 * 支持的占位符列表
 * - {username} - 用户名
 * - {nickname} - 用户昵称
 * - {date} - 当前日期 (YYYY-MM-DD)
 * - {time} - 当前时间 (HH:mm)
 * - {datetime} - 当前日期时间 (YYYY-MM-DD HH:mm)
 * - {year} - 当前年份
 * - {month} - 当前月份
 * - {day} - 当前日期
 */

/**
 * 替换水印文本中的占位符
 * @param text 原始文本（包含占位符）
 * @param userInfo 用户信息
 * @returns 替换后的文本
 */
export function replaceWatermarkPlaceholders(text: string, userInfo?: Partial<Api.Auth.UserInfo>): string {
  if (!text) return ''

  let result = text
  const now = new Date()

  // 用户相关占位符
  if (userInfo) {
    // {username} - 用户名
    if (result.includes('{username}')) {
      result = result.replace(/{username}/g, userInfo.userName || '未知用户')
    }
    
    // {nickname} - 用户昵称
    if (result.includes('{nickname}')) {
      result = result.replace(/{nickname}/g, userInfo.nickName || userInfo.userName || '未知用户')
    }
  }

  // 日期时间相关占位符
  // {year} - 年份
  if (result.includes('{year}')) {
    result = result.replace(/{year}/g, String(now.getFullYear()))
  }

  // {month} - 月份
  if (result.includes('{month}')) {
    result = result.replace(/{month}/g, String(now.getMonth() + 1).padStart(2, '0'))
  }

  // {day} - 日期
  if (result.includes('{day}')) {
    result = result.replace(/{day}/g, String(now.getDate()).padStart(2, '0'))
  }

  // {date} - 完整日期
  if (result.includes('{date}')) {
    const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    result = result.replace(/{date}/g, dateStr)
  }

  // {time} - 时间
  if (result.includes('{time}')) {
    const timeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    result = result.replace(/{time}/g, timeStr)
  }

  // {datetime} - 日期时间
  if (result.includes('{datetime}')) {
    const dateStr = `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
    const timeStr = `${String(now.getHours()).padStart(2, '0')}:${String(now.getMinutes()).padStart(2, '0')}`
    result = result.replace(/{datetime}/g, `${dateStr} ${timeStr}`)
  }

  return result
}

/**
 * 获取支持的占位符列表（用于文档或提示）
 */
export function getSupportedPlaceholders(): Array<{ key: string; description: string }> {
  return [
    { key: '{username}', description: '用户名' },
    { key: '{nickname}', description: '用户昵称' },
    { key: '{date}', description: '当前日期 (YYYY-MM-DD)' },
    { key: '{time}', description: '当前时间 (HH:mm)' },
    { key: '{datetime}', description: '当前日期时间 (YYYY-MM-DD HH:mm)' },
    { key: '{year}', description: '当前年份' },
    { key: '{month}', description: '当前月份' },
    { key: '{day}', description: '当前日期' }
  ]
}
