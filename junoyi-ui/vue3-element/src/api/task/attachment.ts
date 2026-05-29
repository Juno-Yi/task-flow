import request from '@/utils/http'

/**
 * 上传任务附件
 */
export function fetchUploadTaskAttachment(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<Api.Common.FileInfo>({
        url: '/file/task/attachment/upload',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}

/**
 * 删除任务附件
 */
export function fetchDeleteTaskAttachment(filePath: string) {
    return request.del<void>({
        url: `/file/task/attachment/delete?filePath=${encodeURIComponent(filePath)}`
    })
}

