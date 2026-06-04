declare namespace Api.Project {
    /**
     * 项目详情视图对象
     */
    interface ProjectDetailVO {
        /** 项目ID */
        id: number
        /** 项目编号 */
        no: string
        /** 项目名称 */
        name: string
        /** 项目描述 */
        description?: string
        /** 项目负责人ID */
        leader: number
        /** 项目负责人名称 */
        leaderName?: string
        /** 项目类型 */
        type: number
        /** 项目类型标签（字典翻译） */
        typeLabel?: string
        /** 项目类型标签类型（用于前端标签颜色） */
        typeLabelType?: string
        /** 项目状态 */
        status: number
        /** 项目状态标签（字典翻译） */
        statusLabel?: string
        /** 项目状态标签类型（用于前端标签颜色） */
        statusType?: string
        /** 项目优先级 */
        priority?: number
        /** 项目优先级标签（字典翻译） */
        priorityLabel?: string
        /** 项目优先级标签类型（用于前端标签颜色） */
        priorityType?: string
        /** 项目成员数量 */
        memberCount: number
        /** 项目任务数量 */
        taskCount: number
        /** 里程碑数量 */
        milestoneCount: number
        /** 未完成的需求数量 */
        requirementCount: number
        /** 项目进度 */
        progress?: number
        /** 总任务数 */
        totalTasks?: number
        /** 已完成任务数 */
        completedTasks?: number
        /** 待处理任务数 */
        pendingTasks?: number
        /** 仓库数量 */
        repositoryCount?: number
        /** 文档数量 */
        documentCount?: number
        /** 创建者 */
        createBy?: string
        /** 创建时间 */
        createTime?: string
        /** 更新者 */
        updateBy?: string
        /** 更新时间 */
        updateTime?: string
        /** 备注 */
        remark?: string
        /** 最近的项目成员 */
        recentMembers?: ProjectMemberVO[]
        /** 当前用户在项目中的角色 */
        currentUserRole?: string | null
    }

    /**
     * 项目概览数据 VO
     */
    interface ProjectOverviewVO {
        projectActivityTrend: ProjectActivityTrendVO[]
        projectKeyData: ProjectKeyDateVO
        projectRequirementSituation: ProjectRequirementSituationVO[]
        projectRequirementCompletedVO: ProjectRequirementCompletedVO
        projectTaskCompletedVO: ProjectTaskCompletedVO
    }

    /**
     * 项目任务完成情况趋势数据
     */
    interface ProjectTaskCompletedVO {
        sevenDayList: TaskTrendVO[]
        thirtyDayList: TaskTrendVO[]
        ninetyDayList: TaskTrendVO[]
    }

    /**
     * 项目需求情况 VO
     */
    interface ProjectRequirementSituationVO {
        status: number
        statusLabel: string
        statusType: string
        count: number
    }

    /**
     * 项目需求完成情况 VO
     */
    interface ProjectRequirementCompletedVO {
        sevenDayList: RequirementTrendVO[]
        thirtyDayList: RequirementTrendVO[]
        ninetyDayList: RequirementTrendVO[]
    }

    /**
     * 任务完成趋势 VO
     */
    interface TaskTrendVO extends RequirementTrendVO {}

    /**
     * 需求趋势 VO
     *
     * @author Fan
     */
    interface RequirementTrendVO {
        date: string
        count: number
    }

    /**
     * 项目活跃度
     */
    interface ProjectActivityTrendVO {
        date: string
        count: number
    }

    /**
     * 项目关键指标数据 VO
     */
    interface ProjectKeyDateVO {
        projectCompletion: number
        ongoingTasks: number
        pendingRequirements: number
        overdueTasks: number
    }
}