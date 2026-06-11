declare namespace Api.Oauth {

    /**
     * 第三方授权 URL VO
     */
    interface ThirdAuthUrlVO {
        authUrl: string;
        authType: string;
        state: string;
    }

}