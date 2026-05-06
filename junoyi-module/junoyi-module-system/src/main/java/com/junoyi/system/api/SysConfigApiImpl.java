package com.junoyi.system.api;

import com.junoyi.system.service.ISysConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统参数 API 实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysConfigApiImpl implements SysConfigApi {

    private final ISysConfigService sysConfigService;

    @Override
    public String getConfigByKey(String configKey) {
        return sysConfigService.getConfigByKey(configKey);
    }
}

