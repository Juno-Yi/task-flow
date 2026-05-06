package com.junoyi.framework.datasource.aspect;

import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.junoyi.framework.datasource.annotation.DataSource;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 数据源切换切面
 * 通过 AOP 拦截 @DataSource 注解，实现动态数据源切换
 *
 * @author Fan
 */
@Aspect
@Component
@Order(1) // 确保在事务切面之前执行
public class DataSourceAspect {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(DataSourceAspect.class);

    /**
     * 定义切点：拦截所有带有 @DataSource 注解的方法
     */
    @Pointcut("@annotation(com.junoyi.framework.datasource.annotation.DataSource) " +
            "|| @within(com.junoyi.framework.datasource.annotation.DataSource)")
    public void dataSourcePointCut() {
    }

    /**
     * 环绕通知：在方法执行前切换数据源，执行后清除数据源
     *
     * @param point 切点
     * @return 方法执行结果
     * @throws Throwable 方法执行异常
     */
    @Around("dataSourcePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        DataSource dataSource = getDataSource(point);

        if (dataSource != null) {
            String dsKey = dataSource.value().getName();
            DynamicDataSourceContextHolder.push(dsKey);
            log.debug("Switch to datasource: [{}]", dsKey);
        }

        try {
            return point.proceed();
        } finally {
            // 清除数据源，避免内存泄漏
            DynamicDataSourceContextHolder.clear();
            log.debug("Clear datasource");
        }
    }

    /**
     * 获取方法或类上的 @DataSource 注解
     * 优先级：方法注解 > 类注解
     *
     * @param point 切点
     * @return DataSource 注解
     */
    private DataSource getDataSource(ProceedingJoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        // 优先获取方法上的注解
        DataSource dataSource = AnnotationUtils.findAnnotation(method, DataSource.class);
        if (dataSource != null) {
            return dataSource;
        }

        // 获取类上的注解
        Class<?> targetClass = point.getTarget().getClass();
        return AnnotationUtils.findAnnotation(targetClass, DataSource.class);
    }
}
