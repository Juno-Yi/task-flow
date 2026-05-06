package com.junoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysOperLogQueryDTO;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.domain.vo.SysOperLogVO;

/**
 * 操作日志业务接口类
 *
 * @author Fan
 */
public interface ISysOperLogService {

    /**
     * 分页查询操作日志
     *
     * @param queryDTO 查询条件
     * @param page     分页参数
     * @return 分页结果
     */
    PageResult<SysOperLogVO> getOperationLogList(SysOperLogQueryDTO queryDTO, Page<SysOperLog> page);

    /**
     * 删除操作日志
     *
     * @param ids 日志ID数组
     */
    void deleteOperationLog(Long[] ids);

    /**
     * 清空操作日志
     */
    void clearOperationLog();

    /**
     * 记录操作日志
     *
     * @param operationLog 操作日志
     */
    void recordOperationLog(SysOperLog operationLog);

    /**
     * 记录操作日志（简化版）
     *
     * @param level      日志级别
     * @param action     动作
     * @param module     模块
     * @param message    详情描述
     * @param targetId   对象ID
     * @param targetName 对象名称
     * @param rawData    原始数据
     */
    void recordLog(String level, String action, String module, String message, String targetId, String targetName, String rawData);

    /**
     * 记录操作日志（简化版-info级别）
     */
    void recordInfoLog(String action, String module, String message, String targetId, String targetName);
}
