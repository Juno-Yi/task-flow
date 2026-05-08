/**
 * Oauth第三方认证登录
 */
declare namespace Api.Oauth {

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
}