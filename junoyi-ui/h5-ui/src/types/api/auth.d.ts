declare namespace Api.Auth {

    /**
     * 登录参数
     */
    interface LoginParams {
        captchaId: string;
        username: string;
        password: string;
        code: string;
    }

    /**
     * 登录响应
     */
    interface LoginResponse {
        accessToken: string;
        refreshToken: string;
    }

    /**
     * 用户信息
     */
    interface UserInfo {
        userId: number;
        userName: string;
        nickName: string;
        email?: string;
        avatar?: string;
        permissions?: string[];
        roles?: string[];
        depts?: number[];
    }

    /**
     * 验证码响应
     */
    interface CaptchaResponse {
        captchaId: string;
        image: string;
    }
}