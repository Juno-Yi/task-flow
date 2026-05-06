package com.junoyi.system.service;

import com.junoyi.system.domain.vo.SystemMonitorVO;

/**
 * 系统监控服务接口
 *
 * @author Fan
 */
public interface ISysMonitorService {
    
    /**
     * 获取系统监控信息
     *
     * @return 系统监控信息
     */
    SystemMonitorVO getSystemMonitorInfo();
}
