package com.junoyi.system.listener;

import com.junoyi.framework.core.utils.IPUtils;
import com.junoyi.framework.core.utils.UserAgentUtils;
import com.junoyi.framework.event.annotation.EventHandler;
import com.junoyi.framework.event.annotation.EventListener;
import com.junoyi.framework.event.enums.EventPriority;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.system.domain.po.SysAuthLog;
import com.junoyi.system.event.UserLoginEvent;
import com.junoyi.system.service.ISysAuthLogService;
import lombok.RequiredArgsConstructor;

import java.util.Date;

/**
 * 登录日志监听器
 *
 * @author Fan
 */
@EventListener
@RequiredArgsConstructor
public class LoginLogListener {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(LoginLogListener.class);

    private final ISysAuthLogService sysAuthLogService;

    /**
     * 当用户登录事件被触发时候（事件优先级：普通，是否异步：是）
     * @param event 用户登录事件
     */
    @EventHandler(priority = EventPriority.NORMAL, async = true)
    public void onLoginEvent(UserLoginEvent event){
        try {
            SysAuthLog authLog = new SysAuthLog();
            authLog.setUserId(event.getUserId());
            authLog.setUserName(event.getUserName());
            authLog.setNickName(event.getNickName());
            authLog.setLoginIp(event.getLoginIp());
            authLog.setIpRegion(IPUtils.getIpRegion(event.getLoginIp()));
            authLog.setSessionId(event.getSessionId());
            authLog.setIdentity(event.getIdentity());
            authLog.setLoginType(event.getLoginType());
            authLog.setStatus(event.isSuccess() ? 1 : 0);
            authLog.setMsg(event.isSuccess() ? "登录成功" : event.getFailMessage());
            authLog.setLoginTime(Date.from(event.getTimestamp()));

            // 解析User-Agent
            if (event.getUserAgent() != null) {
                authLog.setBrowser(UserAgentUtils.parseBrowser(event.getUserAgent()));
                authLog.setOs(UserAgentUtils.parseOS(event.getUserAgent()));
                authLog.setDeviceType(UserAgentUtils.parseDeviceType(event.getUserAgent()));
            }

            sysAuthLogService.recordLoginLog(authLog);
            log.info("AuthLog", "记录登录日志成功: userName={}, status={}", event.getUserName(), event.isSuccess() ? "成功" : "失败");
        } catch (Exception e){
            log.error("AuthLog", "记录登录日志失败: {}", e.getMessage(), e);
        }
    }
}