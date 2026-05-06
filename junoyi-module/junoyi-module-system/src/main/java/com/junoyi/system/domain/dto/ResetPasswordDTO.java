package com.junoyi.system.domain.dto;

import lombok.Data;

/**
 * 重置密码数据传输对象
 *
 * @author Fan
 */
@Data
public class ResetPasswordDTO {

    /**
     * 新密码
     */
    private String newPassword;
}
