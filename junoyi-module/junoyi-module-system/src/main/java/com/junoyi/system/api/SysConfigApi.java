package com.junoyi.system.api;

/**
 * 系统参数 API 接口
 * 供其他模块调用
 *
 * @author Fan
 */
public interface SysConfigApi {

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值，不存在返回 null
     */
    String getConfigByKey(String configKey);
}
