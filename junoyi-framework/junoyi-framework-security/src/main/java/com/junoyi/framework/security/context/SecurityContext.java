package com.junoyi.framework.security.context;

import com.junoyi.framework.security.module.LoginUser;

/**
 * Security 上下文，用于存储当前线程的登录用户信息
 * <p>
 * 线程安全，每个请求线程独立存储
 * 用法：
 * SecurityContext.set(user); // 设置当前登录用户
 * SecurityContext.get();     // 获取当前登录用户
 * SecurityContext.clear();   // 清理当前线程的用户信息
 * </p>
 *
 * @author Fan
 */
public class SecurityContext {

    private static final ThreadLocal<LoginUser> LOGIN_USER_HOLDER = new ThreadLocal<>();

    private SecurityContext() {
        // 私有构造防止实例化
    }

    /**
     * 设置当前登录用户到上下文
     * @param loginUser 当前登录用户
     */
    public static void set(LoginUser loginUser) {
        LOGIN_USER_HOLDER.set(loginUser);
    }

    /**
     * 获取当前线程的登录用户
     * @return LoginUser，如果未登录返回 null
     */
    public static LoginUser get() {
        return LOGIN_USER_HOLDER.get();
    }

    /**
     * 判断当前线程是否已登录
     * @return true 已登录，false 未登录
     */
    public static boolean isLogin() {
        return LOGIN_USER_HOLDER.get() != null;
    }

    /**
     * 清理当前线程的登录用户信息
     * 建议在请求结束后调用，防止内存泄漏
     */
    public static void clear() {
        LOGIN_USER_HOLDER.remove();
    }
}