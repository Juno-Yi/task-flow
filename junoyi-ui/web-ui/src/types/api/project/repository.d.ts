
declare namespace Api.Project {
    /**
     * 项目仓库视图对象
     */
    interface ProjectRepositoryVO {
        /** 仓库ID */
        id: number
        /** 项目ID */
        projectId: number
        /** 仓库名称 */
        name: string
        /** 仓库平台：gitee/github/gitlab/custom */
        type: string
        /** 仓库平台标签 */
        typeLabel?: string
        /** 仓库地址 */
        url: string
        /** 默认分支 */
        branch?: string
        /** 仓库描述 */
        description?: string
        /** 是否主仓库 */
        isMain: boolean
        /** 状态：0-禁用 1-正常 */
        status: number
        /** 状态标签 */
        statusLabel?: string
        /** 备注 */
        remark?: string
        /** 创建者 */
        createBy?: string
        /** 创建时间 */
        createTime?: string
        /** 更新者 */
        updateBy?: string
        /** 更新时间 */
        updateTime?: string
    }

    /**
     * 项目仓库传输数据
     */
    interface ProjectRepositoryDTO {
        /** 仓库ID（修改时必填） */
        id?: number
        /** 项目ID */
        projectId: number
        /** 仓库名称 */
        name: string
        /** 仓库平台：gitee/github/gitlab/custom */
        type: string
        /** 仓库地址 */
        url: string
        /** 默认分支 */
        branch?: string
        /** 仓库描述 */
        description?: string
        /** 是否主仓库 */
        isMain: boolean
        /** 状态：0-禁用 1-正常 */
        status: number
        /** 备注 */
        remark?: string
    }



}
