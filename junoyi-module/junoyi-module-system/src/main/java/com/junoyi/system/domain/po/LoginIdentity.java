package com.junoyi.system.domain.po;

import com.junoyi.system.enums.LoginType;
import lombok.Data;

/**
 * 登录标识符对象
 *
 * @author Fan
 */
@Data
public class LoginIdentity {

    /**
     * 登录方式
     */
    private LoginType loginType;

    /**
     * 登录账号
     */
    private String account;


    public LoginIdentity(LoginType loginType, String account){
        this.loginType = loginType;
        this.account = account;
    }
}