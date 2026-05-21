import request from '@/utils/http'

/**
 * 获取项目成员列表
 */
export function fetchGetProjectMembers(projectId: number) {
    return request.get<Api.Project.ProjectMemberVO[]>({
        url: `/project/member/list/${projectId}`
    })
}


/**
 * 获取项目成员下拉列表（支持昵称模糊搜索）
 */
export function fetchGetProjectMemberOptions(projectId: number, nickName?: string) {
    return request.get<Api.System.SysUserVO[]>({
        url: '/project/member/options',
        params: {
            projectId,
            nickName: nickName || undefined
        }
    })
}


/**
 * 添加项目成员
 */
export function fetchAddProjectMember(data: Api.Project.ProjectMemberAddDTO) {
    return request.post<void>({
        url: '/project/member/add',
        data
    })
}

/**
 * 移除项目成员
 */
export function fetchRemoveMember(memberId: number) {
    return request.del<void>({
        url: `/project/member/${memberId}`
    })
}

/**
 * 更新成员角色
 */
export function fetchUpdateMemberRole(data: Api.Project.ProjectMemberUpdateRoleDTO) {
    return request.put<void>({
        url: '/project/member/role',
        data
    })
}
