declare namespace Api.Oauth {

    /**
     * 企业微信登录配置 VO
     */
    interface WeWorkConfigVO {
        corpId: string;
        agentId: string;
        redirectUri: string;
        state: string;
    }

    /**
     * 企业微信登录/绑定响应
     */
    interface WeWorkLoginResponse {
        accessToken?: string;
        refreshToken?: string;
        needBind?: boolean;
        code?: string;
        weWorkUserId?: string;
    }

    /**
     * 绑定企业微信账号参数
     */
    interface BindWeWorkAccountParams {
        username: string;
        password: string;
        code: string;
        captchaId: string;
        captchaCode: string;
    }

}