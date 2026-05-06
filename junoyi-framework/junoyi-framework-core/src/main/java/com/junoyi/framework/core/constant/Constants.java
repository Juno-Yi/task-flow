package com.junoyi.framework.core.constant;

/**
 * 通用常量名
 *
 * @author Fan
 */
public interface Constants {

    String UTF8 = "UTF-8";

    /**
     * Token类型常量定义
     */
    // Token 类型常量
    String TOKEN_TYPE_ACCESS = "access";
    String TOKEN_TYPE_REFRESH = "refresh";

    /**
     * JWT Claim键名常量定义
     */
    // Claim 键名常量
    String CLAIM_TYPE = "type";
    String CLAIM_TOKEN_ID = "tid";      // 关联 AccessToken 和 RefreshToken
    String CLAIM_JTI = "jti";           // 单个 Token 的唯一标识
    String CLAIM_PLATFORM = "platform";
    String CLAIM_USERNAME = "username";
    String CLAIM_NICK_NAME = "nickName";
    String CLAIM_PERMISSIONS = "perms";  // 权限列表
    String CLAIM_ROLES = "roles";        // 角色列表
}
