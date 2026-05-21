import request from '@/utils/http'

/**
 * 获取项目成员列表
 */
export function fetchGetProjectMembers(projectId: number) {
    return request.get<Api.Project.ProjectMemberVO[]>({
        url: `/project/member/list/${projectId}`
    })
}
