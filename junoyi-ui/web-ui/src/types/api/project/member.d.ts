/**
 * 项目仓库相关类型定义
 */
declare namespace Api.Project {
    /**
     * 项目成员视图对象
     */
    interface ProjectMemberVO {
        /** 成员ID */
        id: number
        /** 用户ID */
        userId: number
        /** 用户名 */
        userName: string
        /** 昵称 */
        nickName: string
        /** 头像 */
        avatar?: string
        /** 项目角色 */
        role: string
        /** 状态 */
        status: number
        /** 加入时间 */
        joinTime: string
        /** 离开时间 */
        leaveTime?: string
    }

    /**
     * 添加项目成员DTO
     */
    interface ProjectMemberAddDTO {
        projectId: number
        userId: number
        role: string
    }

    /**
     * 修改项目成员角色 DTO
     */
    interface ProjectMemberUpdateRoleDTO {
        projectId: number
        memberId: number
        role: string
    }
}