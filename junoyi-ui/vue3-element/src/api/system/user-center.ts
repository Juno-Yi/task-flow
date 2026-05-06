import request from '@/utils/http'

/**
 * 获取当前用户个人信息
 */
export function fetchGetProfile() {
    return request.get<Api.System.SysUserVO>({
        url: '/system/user-center/profile'
    })
}

/**
 * 更新当前用户个人信息
 */
export function fetchUpdateProfile(data: Api.System.UserProfileDTO) {
    return request.put<void>({
        url: '/system/user-center/profile',
        data,
    })
}

/**
 * 更新当前用户头像
 */
export function fetchUpdateAvatar(avatar: string) {
    return request.put<string>({
        url: '/system/user-center/avatar',
        data: { avatar },
    })
}

/**
 * 修改当前用户密码
 */
export function fetchChangePassword(data: Api.System.ChangePasswordDTO) {
    return request.put<void>({
        url: '/system/user-center/password',
        data,
    })
}

/**
 * 上传头像文件
 */
export function fetchUploadAvatar(file: File) {
    const formData = new FormData()
    formData.append('file', file)
    return request.post<Api.Common.FileInfo>({
        url: '/upload/avatar',
        data: formData,
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    })
}
