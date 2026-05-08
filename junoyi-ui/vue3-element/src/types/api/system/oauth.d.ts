/**
 * Oauth第三方认证登录
 */
declare namespace Api.Oauth {

    /**
     * Oauth配置查询参数
     */
    interface OauthConfigQueryParams extends Api.Common.PageParams {
        /** 平台 */
        platform?: string
        /** 状态 */
        status?: number
        /** 平台名称（模糊查询） */
        platformName?: string
    }

    /**
     * Oauth配置VO
     */
    interface OauthConfigVO {
        id: number
        platform: string
        platformLabel: string
        status: number
        statusLabel: string
        statusType: string
        redirectUrl: string
        isSystem: boolean
        createTime: string
        updateTime: string
        remark: string
    }

    /**
     * Oauth配置表单
     */
    interface OauthConfigForm {
        id?: number
        platform: string
        status: number
        redirectUrl: string
        remark?: string
    }
}