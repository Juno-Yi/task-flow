package com.junoyi.system.domain.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 用户个人信息更新 DTO
 *
 * @author Fan
 */
@Data
public class UserProfileDTO {
    /**
     * 昵称
     */
    @Size(max = 50, message = "昵称长度不能超过50个字符")
    private String nickName;

    /**
     * 手机号
     */
    @Size(max = 20, message = "手机号长度不能超过20个字符")
    private String phonenumber;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100个字符")
    private String email;

    /**
     * 性别（0-未知，1-男，2-女）
     */
    private String sex;

    /**
     * 头像URL
     */
    private String avatar;
}