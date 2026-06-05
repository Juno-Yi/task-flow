package com.junoyi.system.convert;

import com.junoyi.system.domain.bo.LoginBO;
import com.junoyi.system.domain.dto.LoginDTO;

/**
 * 登录对象转换器（静态工具类）
 * <p>
 * 提供登录相关对象之间的转换功能，主要用于将前端传入的登录请求数据（DTO）
 * 转换为业务层使用的登录对象（BO），实现数据传输层与业务逻辑层的解耦。
 * </p>
 *
 * @author Fan
 */
public final class LoginConverter {

    /**
     * 私有构造函数，防止实例化
     * <p>
     * 抛出异常是为了防止通过反射方式实例化该类，确保工具类的纯粹性。
     * </p>
     */
    private LoginConverter() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 将登录传输对象（DTO）转换为业务对象（BO）
     * <p>
     * 该方法主要用于将控制层接收的前端登录请求数据转换为业务层处理的登录对象。
     * 转换过程中会复制以下字段：
     * - 用户名（username）
     * - 邮箱地址（email）
     * - 手机号码（phonenumber）
     * - 密码（password）
     * - 登录平台类型（platformType）
     * </p>
     * <p>
     * 注意：DTO 中的 captchaId 和 code 字段（验证码相关信息）不会被复制到 BO 中，
     * 这些字段通常在验证阶段使用，验证通过后不再传递到业务层。
     * </p>
     *
     * @param loginDTO 登录传输对象，包含前端传入的完整登录信息（包括验证码相关字段）
     * @return 转换后的登录业务对象，用于后续的业务逻辑处理
     */
    public static LoginBO toLoginBo(LoginDTO loginDTO){
        LoginBO loginBO = new LoginBO();
        loginBO.setEmail(loginDTO.getEmail());
        loginBO.setPhonenumber(loginBO.getPhonenumber());
        loginBO.setUsername(loginDTO.getUsername());
        loginBO.setPlatformType(loginDTO.getPlatformType());
        loginBO.setPassword(loginDTO.getPassword());
        return loginBO;
    }
}
