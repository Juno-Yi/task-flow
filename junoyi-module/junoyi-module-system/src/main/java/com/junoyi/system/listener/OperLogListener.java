package com.junoyi.system.listener;

import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.event.enums.EventPriority;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.domain.po.SysOperLog;
import com.junoyi.system.event.UserOperationEvent;
import com.junoyi.system.service.ISysOperLogService;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * 操作日志事件监听器
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class OperLogListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(OperLogListener.class);

    private final ISysOperLogService sysOperLogService;

    /**
     * 当用户操作事件触发（优先级：正常，异步：开启）
     * @param event 用户操作事件
     */
    @EventHandler(priority = EventPriority.NORMAL, async = true)
    public void onOperationLogEvent(UserOperationEvent event) {
        try {
            SysOperLog operationLog = new SysOperLog();
            operationLog.setLevel(event.getLevel());
            operationLog.setAction(event.getAction());
            operationLog.setModule(event.getModule());
            operationLog.setMessage(event.getMessage());
            operationLog.setTargetId(event.getTargetId());
            operationLog.setTargetName(event.getTargetName());
            operationLog.setRawData(event.getRawData());
            operationLog.setCreateTime(Date.from(event.getTimestamp()));

            // 用户信息（已在事件创建时获取）
            operationLog.setUserId(event.getUserId());
            operationLog.setUserName(event.getUserName());
            operationLog.setNickName(event.getNickName());

            // 请求信息（已在事件创建时获取）
            operationLog.setIp(event.getIp());
            operationLog.setPath(event.getPath());
            operationLog.setMethod(event.getMethod());

            sysOperLogService.recordOperationLog(operationLog);
            log.debug("OperationLog", "记录操作日志成功: action={}, module={}, message={}",
                    event.getAction(), event.getModule(), event.getMessage());
        } catch (Exception e) {
            log.error("OperationLog", "记录操作日志失败: {}", e.getMessage(), e);
        }
    }
}