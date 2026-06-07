package com.junoyi.oauth.domain.dto;


import lombok.Data;

/**
 * Oauth绑定登录参数 DTO
 *
 * @author Fan
 */
@Data
public class BindOauthParamsDTO {

    private String username;

    private String password;

    private String code;

    private String captchaId;

    private String captchaCode;
}