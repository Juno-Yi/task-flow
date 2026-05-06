package com.junoyi.system.service;

import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.dto.SysConfigQueryDTO;
import com.junoyi.system.domain.vo.SysConfigVO;

import java.util.List;

/**
 * 系统参数配置业务接口
 *
 * @author Fan
 */
public interface ISysConfigService {

    /**
     * 获取系统参数列表（分页）
     *
     * @param queryDTO 查询参数
     * @return 分页结果
     */
    PageResult<SysConfigVO> getConfigList(SysConfigQueryDTO queryDTO);

    /**
     * 根据ID获取系统参数详情
     *
     * @param id 参数ID
     * @return 参数详情
     */
    SysConfigVO getConfigById(Long id);

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值
     */
    String getConfigByKey(String configKey);

    /**
     * 添加系统参数
     *
     * @param configDTO 参数信息
     */
    void addConfig(SysConfigDTO configDTO);

    /**
     * 更新系统参数
     *
     * @param configDTO 参数信息
     */
    void updateConfig(SysConfigDTO configDTO);

    /**
     * 删除系统参数
     *
     * @param id 参数ID
     */
    void deleteConfig(Long id);

    /**
     * 批量删除系统参数
     *
     * @param ids 参数ID列表
     */
    void deleteConfigBatch(List<Long> ids);

    /**
     * 刷新系统参数缓存
     */
    void refreshCache();

    /**
     * 根据参数键名列表批量获取参数配置
     *
     * @param configKeys 参数键名列表
     * @return 参数配置列表
     */
    List<SysConfigVO> getConfigsByKeys(List<String> configKeys);
}
