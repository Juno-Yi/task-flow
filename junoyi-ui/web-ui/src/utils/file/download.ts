/**
 * 文件下载工具函数
 */

/**
 * 从响应头中提取文件名
 * @param contentDisposition Content-Disposition 响应头
 * @param defaultFilename 默认文件名
 * @returns 文件名
 */
export function getFilenameFromContentDisposition(
    contentDisposition: string | undefined,
    defaultFilename: string = 'download'
): string {
    if (!contentDisposition) {
        return defaultFilename
    }

    // 支持多种文件名编码格式
    const filenameRegex = /filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/
    const matches = filenameRegex.exec(contentDisposition)

    if (matches && matches[1]) {
        let filename = matches[1].replace(/['"]/g, '')

        // 尝试解码 URL 编码的文件名
        try {
            filename = decodeURIComponent(filename)
        } catch (e) {
            // 如果解码失败，使用原始文件名
            console.warn('Failed to decode filename:', e)
        }

        return filename
    }

    return defaultFilename
}

/**
 * 下载 Blob 数据为文件
 * @param blob Blob 数据
 * @param filename 文件名
 * @param mimeType MIME 类型
 */
export function downloadBlob(
    blob: Blob,
    filename: string,
    mimeType?: string
): void {
    // 如果指定了 MIME 类型，创建新的 Blob
    const downloadBlob = mimeType
        ? new Blob([blob], { type: mimeType })
        : blob

    // 创建下载链接
    const url = window.URL.createObjectURL(downloadBlob)
    const link = document.createElement('a')
    link.href = url
    link.download = filename
    link.style.display = 'none'

    // 触发下载
    document.body.appendChild(link)
    link.click()

    // 清理资源
    setTimeout(() => {
        document.body.removeChild(link)
        window.URL.revokeObjectURL(url)
    }, 100)
}

/**
 * 下载文件（通用方法）
 * @param blob Blob 数据
 * @param contentDisposition Content-Disposition 响应头
 * @param defaultFilename 默认文件名
 * @param mimeType MIME 类型
 */
export function downloadFile(
    blob: Blob,
    contentDisposition?: string,
    defaultFilename: string = 'download',
    mimeType?: string
): void {
    const filename = getFilenameFromContentDisposition(contentDisposition, defaultFilename)
    downloadBlob(blob, filename, mimeType)
}

/**
 * 下载 Excel 文件
 * @param blob Blob 数据
 * @param contentDisposition Content-Disposition 响应头
 * @param defaultFilename 默认文件名
 */
export function downloadExcel(
    blob: Blob,
    contentDisposition?: string,
    defaultFilename: string = 'export.xlsx'
): void {
    downloadFile(
        blob,
        contentDisposition,
        defaultFilename,
        'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
    )
}

