package com.junoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.log.core.JunoYiLog;
import com.junoyi.framework.log.core.JunoYiLogFactory;
import com.junoyi.framework.redis.utils.RedisUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.system.convert.SysConfigConverter;
import com.junoyi.system.domain.dto.SysConfigDTO;
import com.junoyi.system.domain.dto.SysConfigQueryDTO;
import com.junoyi.system.domain.po.SysConfig;
import com.junoyi.system.domain.vo.SysConfigVO;
import com.junoyi.system.enums.ConfigType;
import com.junoyi.system.event.ConfigChangedEvent;
import com.junoyi.system.exception.ConfigKeyExistsException;
import com.junoyi.system.exception.ConfigNotFoundException;
import com.junoyi.system.exception.ConfigSystemProtectedException;
import com.junoyi.system.exception.ConfigTypeInvalidException;
import com.junoyi.system.mapper.SysConfigMapper;
import com.junoyi.system.service.ISysConfigService;
import com.junoyi.system.util.ConfigValueValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

/**
 * 系统参数配置业务实现类
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class SysConfigServiceImpl implements ISysConfigService {

    private final JunoYiLog log = JunoYiLogFactory.getLogger(SysConfigServiceImpl.class);
    private final SysConfigMapper sysConfigMapper;
    private final SysConfigConverter sysConfigConverter;

    private static final String CACHE_KEY_PREFIX = "sys:config:";
    private static final long CACHE_EXPIRE_HOURS = 24;

    /**
     * 分页查询系统参数配置列表
     *
     * @param queryDTO 查询条件DTO
     * @return 分页结果对象，包含VO列表、总数、当前页码、每页大小
     */
    @Override
    public PageResult<SysConfigVO> getConfigList(SysConfigQueryDTO queryDTO) {
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(queryDTO.getConfigName()), SysConfig::getConfigName, queryDTO.getConfigName())
                .like(StringUtils.isNotBlank(queryDTO.getConfigKey()), SysConfig::getConfigKey, queryDTO.getConfigKey())
                .eq(StringUtils.isNotBlank(queryDTO.getConfigType()), SysConfig::getConfigType, queryDTO.getConfigType())
                .eq(queryDTO.getIsSystem() != null, SysConfig::getIsSystem, queryDTO.getIsSystem())
                .orderByAsc(SysConfig::getSort)
                .orderByDesc(SysConfig::getCreateTime);

        Page<SysConfig> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        Page<SysConfig> resultPage = sysConfigMapper.selectPage(page, wrapper);

        List<SysConfigVO> voList = sysConfigConverter.toVoList(resultPage.getRecords());
        return PageResult.of(voList, resultPage.getTotal(), (int) resultPage.getCurrent(), (int) resultPage.getSize());
    }

    /**
     * 根据ID获取系统参数配置信息
     *
     * @param id 参数配置ID
     * @return 系统参数配置VO对象，如果不存在则返回null
     */
    @Override
    public SysConfigVO getConfigById(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        return config != null ? sysConfigConverter.toVo(config) : null;
    }

    /**
     * 根据参数键名获取参数值
     *
     * @param configKey 参数键名
     * @return 参数值，如果不存在则返回null
     */
    @Override
    public String getConfigByKey(String configKey) {
        // 先从缓存获取
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        String cachedValue = RedisUtils.getCacheObject(cacheKey);
        if (cachedValue != null) {
            return cachedValue;
        }

        // 缓存未命中，从数据库查询
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configKey)
                .eq(SysConfig::getStatus, 0);
        SysConfig config = sysConfigMapper.selectOne(wrapper);

        if (config != null) {
            String value = config.getConfigValue();
            // 存入缓存（24小时过期）
            RedisUtils.setCacheObject(cacheKey, value, Duration.ofHours(CACHE_EXPIRE_HOURS));
            return value;
        }

        return null;
    }

    /**
     * 新增系统参数配置
     *
     * @param configDTO 系统参数配置DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(SysConfigDTO configDTO) {
        // 检查键名是否已存在
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysConfig::getConfigKey, configDTO.getConfigKey());
        Long count = sysConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ConfigKeyExistsException(configDTO.getConfigKey());
        }

        // 验证配置类型
        String configType = configDTO.getConfigType();
        if (StringUtils.isNotBlank(configType) && !ConfigType.isValid(configType)) {
            throw new ConfigTypeInvalidException(configType);
        }

        // 验证配置值
        ConfigValueValidator.validate(
                configType != null ? configType : ConfigType.TEXT.getCode(),
                configDTO.getConfigValue()
        );

        SysConfig config = sysConfigConverter.toEntity(configDTO);

        // 设置默认值
        if (config.getIsSystem() == null)
            config.setIsSystem(0);
        if (config.getStatus() == null)
            config.setStatus(0);
        if (config.getSort() == null)
            config.setSort(0);
        if (config.getConfigType() == null)
            config.setConfigType(ConfigType.TEXT.getCode());
        config.setCreateBy(SecurityUtils.getUserName());
        config.setCreateTime(DateUtils.getNowDate());

        sysConfigMapper.insert(config);

        // 清除缓存
        clearCache(config.getConfigKey());

        // 发布配置变更事件
        publishConfigChangedEvent(
                config.getConfigKey(),
                null,
                config.getConfigValue(),
                ConfigChangedEvent.OperationType.ADD
        );

        log.info("Config", "添加系统参数: {}", config.getConfigKey());
    }

    /**
     * 更新系统参数配置
     *
     * @param configDTO 系统参数配置DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(SysConfigDTO configDTO) {
        SysConfig oldConfig = sysConfigMapper.selectById(configDTO.getConfigId());
        if (oldConfig == null) {
            throw new ConfigNotFoundException();
        }

        // 系统内置参数不允许修改键名
        if (oldConfig.getIsSystem() == 1 && !oldConfig.getConfigKey().equals(configDTO.getConfigKey())) {
            throw new ConfigSystemProtectedException("系统内置参数不允许修改键名");
        }

        // 验证配置类型
        String configType = configDTO.getConfigType();
        if (StringUtils.isNotBlank(configType) && !ConfigType.isValid(configType)) {
            throw new ConfigTypeInvalidException(configType);
        }

        // 验证配置值
        ConfigValueValidator.validate(
                configType != null ? configType : oldConfig.getConfigType(),
                configDTO.getConfigValue()
        );

        // 转换DTO到实体
        SysConfig config = sysConfigConverter.toEntity(configDTO);
        config.setConfigId(configDTO.getConfigId());

        // 保留原有的字段值（DTO中没有的字段）
        config.setIsSystem(oldConfig.getIsSystem()); // 不允许修改是否为系统内置
        if (config.getConfigType() == null)
            config.setConfigType(oldConfig.getConfigType());
        if (config.getSort() == null)
            config.setSort(oldConfig.getSort());
        if (config.getStatus() == null)
            config.setStatus(oldConfig.getStatus());
        config.setUpdateBy(SecurityUtils.getUserName());
        config.setUpdateTime(DateUtils.getNowDate());

        sysConfigMapper.updateById(config);

        // 清除缓存
        clearCache(oldConfig.getConfigKey());
        if (!oldConfig.getConfigKey().equals(config.getConfigKey())) {
            clearCache(config.getConfigKey());
        }

        // 发布配置变更事件
        publishConfigChangedEvent(
                config.getConfigKey(),
                oldConfig.getConfigValue(),
                config.getConfigValue(),
                ConfigChangedEvent.OperationType.UPDATE
        );

        log.info("Config", "更新系统参数: {}", config.getConfigKey());
    }

    /**
     * 删除系统参数配置
     *
     * @param id 系统参数配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        SysConfig config = sysConfigMapper.selectById(id);
        if (config == null) {
            throw new ConfigNotFoundException();
        }

        // 系统内置参数不允许删除
        if (config.getIsSystem() == 1) {
            throw new ConfigSystemProtectedException("系统内置参数不允许删除");
        }

        sysConfigMapper.deleteById(id);

        // 清除缓存
        clearCache(config.getConfigKey());

        // 发布配置变更事件
        publishConfigChangedEvent(
                config.getConfigKey(),
                config.getConfigValue(),
                null,
                ConfigChangedEvent.OperationType.DELETE
        );

        log.info("Config", "删除系统参数: {}", config.getConfigKey());
    }

    /**
     * 批量删除系统参数配置
     *
     * @param ids 系统参数配置ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigBatch(List<Long> ids) {
        // 检查是否包含系统内置参数
        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysConfig::getConfigId, ids)
                .eq(SysConfig::getIsSystem, 1);
        Long count = sysConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new ConfigSystemProtectedException("不能删除系统内置参数");
        }

        // 获取所有配置的键名，用于清除缓存和发布事件
        List<SysConfig> configs = sysConfigMapper.selectBatchIds(ids);

        // 批量删除
        for (Long id : ids) {
            sysConfigMapper.deleteById(id);
        }

        // 清除缓存并发布事件
        configs.forEach(config -> {
            clearCache(config.getConfigKey());
            publishConfigChangedEvent(
                    config.getConfigKey(),
                    config.getConfigValue(),
                    null,
                    ConfigChangedEvent.OperationType.DELETE
            );
        });

        log.info("Config", "批量删除系统参数: {} 条", ids.size());
    }

    /**
     * 刷新系统参数缓存
     */
    @Override
    public void refreshCache() {
        // 清除所有配置缓存
        RedisUtils.deleteKeys(CACHE_KEY_PREFIX + "*");
        log.info("Config", "刷新系统参数缓存");
    }

    /**
     * 根据参数键名列表批量获取参数配置
     *
     * @param configKeys 参数键名列表
     * @return 参数配置列表
     */
    @Override
    public List<SysConfigVO> getConfigsByKeys(List<String> configKeys) {
        if (configKeys == null || configKeys.isEmpty()) {
            return List.of();
        }

        LambdaQueryWrapper<SysConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysConfig::getConfigKey, configKeys)
                .eq(SysConfig::getStatus, 0)
                .orderByAsc(SysConfig::getSort);

        List<SysConfig> configs = sysConfigMapper.selectList(wrapper);
        return sysConfigConverter.toVoList(configs);
    }

    /**
     * 清除指定键名的缓存
     *
     * @param configKey 配置键名
     */
    private void clearCache(String configKey) {
        String cacheKey = CACHE_KEY_PREFIX + configKey;
        RedisUtils.deleteObject(cacheKey);
    }

    /**
     * 发布配置变更事件
     *
     * @param configKey     配置键名
     * @param oldValue      旧值
     * @param newValue      新值
     * @param operationType 操作类型
     */
    private void publishConfigChangedEvent(String configKey, String oldValue, String newValue,
                                            ConfigChangedEvent.OperationType operationType) {
        try {
            String operator = SecurityUtils.getUserName();
            ConfigChangedEvent event = new ConfigChangedEvent(
                    this,
                    configKey,
                    oldValue,
                    newValue,
                    operationType,
                    operator
            );
            EventBus.get().callEvent(event);
            log.debug("Config", "发布配置变更事件: key={}, type={}", configKey, operationType);
        } catch (Exception e) {
            log.error("Config", "发布配置变更事件失败: " + e.getMessage(), e);
        }
    }
}
