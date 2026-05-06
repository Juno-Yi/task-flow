package com.junoyi.framework.permission.aspect;

import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.permission.annotation.Permission;
import com.junoyi.framework.permission.enums.Logical;
import com.junoyi.framework.permission.exception.NoPermissionException;
import com.junoyi.framework.permission.exception.NotLoginException;
import com.junoyi.framework.permission.helper.PermissionHelper;
import com.junoyi.framework.permission.properties.PermissionProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * 权限校验切面
 * <p>
 * 拦截带有 @Permission 注解的方法，进行权限校验
 *
 * @author Fan
 */
@Aspect
@Order(100)
@RequiredArgsConstructor
public class PermissionAspect {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(PermissionProperties.class);

    private final PermissionProperties properties;

    /**
     * 方法级别权限校验切面
     * 拦截带有 @Permission 注解的方法，进行权限校验
     *
     * @param point 切点信息
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@annotation(com.junoyi.framework.permission.annotation.Permission)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        // 检查是否启用权限控制
        if (!properties.isEnable()) {
            return point.proceed();
        }

        // 获取方法上的注解
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        Permission permission = method.getAnnotation(Permission.class);

        // 校验权限
        checkPermission(permission);

        return point.proceed();
    }

    /**
     * 类级别权限校验切面
     * 拦截类上带有 @Permission 注解的方法，进行权限校验
     *
     * @param point 切点信息
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("@within(com.junoyi.framework.permission.annotation.Permission)")
    public Object aroundClass(ProceedingJoinPoint point) throws Throwable {
        // 检查是否启用权限控制
        if (!properties.isEnable()) {
            return point.proceed();
        }

        // 获取类上的注解
        Class<?> targetClass = point.getTarget().getClass();
        Permission permission = targetClass.getAnnotation(Permission.class);

        // 方法上的注解优先级更高，如果方法上有注解则跳过类级别校验
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        if (method.isAnnotationPresent(Permission.class)) {
            return point.proceed();
        }

        // 校验权限
        checkPermission(permission);

        return point.proceed();
    }

    /**
     * 校验权限
     *
     * @param permission 权限注解信息
     */
    private void checkPermission(Permission permission) {
        if (permission == null) {
            return;
        }

        // 检查是否需要登录
        if (permission.requireLogin()) {
            Long userId = PermissionHelper.getCurrentUserId();
            if (userId == null) {
                throw new NotLoginException();
            }
        }

        // 超级管理员跳过权限校验
        if (PermissionHelper.isSuperAdmin()) {
            return;
        }

        // 获取需要的权限
        String[] requiredPermissions = permission.value();
        if (requiredPermissions.length == 0) {
            return;
        }

        // 根据逻辑类型校验
        Logical logical = permission.logical();
        boolean hasPermission = PermissionHelper.hasPermissions(requiredPermissions, logical);

        if (!hasPermission) {
            log.warn("权限校验失败，需要权限: {}, 逻辑: {}", requiredPermissions, logical);
            throw new NoPermissionException(requiredPermissions);
        }
    }
}
