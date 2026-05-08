package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.DateUtils;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.framework.event.core.EventBus;
import com.junoyi.framework.json.utils.JsonUtils;
import com.junoyi.framework.security.utils.SecurityUtils;
import com.junoyi.oauth.convert.OauthConfigConverter;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthConfig;
import com.junoyi.oauth.domain.po.OauthPlatform;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.mapper.OauthConfigMapper;
import com.junoyi.oauth.mapper.OauthPlatformMapper;
import com.junoyi.oauth.service.IOauthConfigService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import com.junoyi.system.event.UserOperationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Oauth配置业务接口实现
 *
 * @author Fan
 */
@Service
@RequiredArgsConstructor
public class OauthConfigServiceImpl implements IOauthConfigService {

    private final OauthPlatformMapper oauthPlatformMapper;
    private final OauthConfigMapper oauthConfigMapper;
    private final SysDictApi sysDictApi;

    /**
     * 分页查询Oauth平台配置
     *
     * @param queryDTO 查询条件
     * @param page 分页对象
     * @return Oauth平台配置分页结果
     */
    @Override
    public PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO, Page<OauthPlatform> page) {
        // 构建查询条件
        LambdaQueryWrapper<OauthPlatform> wrapper = new LambdaQueryWrapper<>();

        // 平台精确查询
        wrapper.eq(StringUtils.isNotBlank(queryDTO.getPlatform()),
                   OauthPlatform::getPlatform, queryDTO.getPlatform())
               // 状态查询
               .eq(queryDTO.getStatus() != null,
                   OauthPlatform::getStatus, queryDTO.getStatus())
               // 排序
               .orderByAsc(OauthPlatform::getId);

        // 分页查询
        Page<OauthPlatform> resultPage = oauthPlatformMapper.selectPage(page, wrapper);

        // 转换为VO并填充额外信息
        List<OauthConfigVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 如果有平台名称模糊查询，需要在内存中过滤（因为platformLabel是字典翻译后的值）
        if (StringUtils.isNotBlank(queryDTO.getPlatformName())) {
            voList = voList.stream()
                    .filter(vo -> vo.getPlatformLabel() != null &&
                                  vo.getPlatformLabel().contains(queryDTO.getPlatformName()))
                    .collect(Collectors.toList());
        }

        return PageResult.of(
                voList,
                resultPage.getTotal(),
                (int) resultPage.getCurrent(),
                (int) resultPage.getSize()
        );
    }

    /**
     * 转换PO为VO并填充额外信息
     */
    private OauthConfigVO convertToVO(OauthPlatform platform) {
        OauthConfigVO vo = OauthConfigConverter.INSTANCE.toVO(platform);

        // 从字典获取平台标签
        String platformLabel = sysDictApi.getDictLabel("oauth_platform", platform.getPlatform());
        vo.setPlatformLabel(platformLabel != null ? platformLabel : platform.getPlatform());

        // 从字典获取状态信息
        String statusValue = String.valueOf(platform.getStatus());
        SysDictDataVO statusDict = sysDictApi.getDictItem("oauth_status", statusValue);
        if (statusDict != null) {
            vo.setStatusLabel(statusDict.getDictLabel());
            vo.setStatusType(statusDict.getListClass());
        } else {
            // 默认值
            vo.setStatusLabel(platform.getStatus() == 1 ? "启用" : "禁用");
            vo.setStatusType(platform.getStatus() == 1 ? "success" : "danger");
        }

        return vo;
    }

    /**
     * 添加 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOauthConfig(OauthConfigDTO dto) {
        // 创建 OAuth 平台记录
        OauthPlatform platform = new OauthPlatform();
        platform.setPlatform(dto.getPlatform());
        platform.setStatus(dto.getStatus());
        platform.setRedirectUri(dto.getRedirectUrl());
        platform.setIsSystem(false); // 用户创建的配置默认非系统内置
        platform.setCreateBy(SecurityUtils.getUserName());
        platform.setCreateTime(DateUtils.getNowDate());
        platform.setRemark(dto.getRemark());

        oauthPlatformMapper.insert(platform);

        // 创建 OAuth 平台对应的配置
        OauthConfig oauthConfig = new OauthConfig();
        oauthConfig.setPlatform(dto.getPlatform());
        oauthConfig.setConfigKey(dto.getConfigKey());
        oauthConfig.setConfigValue(dto.getConfigValue());
        oauthConfig.setCreateBy(SecurityUtils.getUserName());
        oauthConfig.setCreateTime(DateUtils.getNowDate());
        platform.setRemark(dto.getRemark());

        oauthConfigMapper.insert(oauthConfig);

        String platformLabel = sysDictApi.getDictLabel("oauth_platform", dto.getPlatform());
        String displayName = platformLabel != null ? platformLabel : dto.getPlatform();

        EventBus.get().callEvent(UserOperationEvent.withRawData(
                "create",
                "oauth_config",
                "创建了OAuth配置「" + displayName + "」",
                String.valueOf(platform.getId()),
                displayName,
                JsonUtils.toJsonString(dto)
        ));
    }

    /**
     * 更新 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOauthConfig(OauthConfigDTO dto) {
        // 1. 查询原有配置
        OauthPlatform existingPlatform = oauthPlatformMapper.selectById(dto.getId());
        if (existingPlatform == null) {
            throw new RuntimeException("OAuth配置不存在");
        }

        // 2. 更新 OAuth 平台记录
        OauthPlatform platform = new OauthPlatform();
        platform.setId(dto.getId());
        platform.setPlatform(dto.getPlatform());
        platform.setStatus(dto.getStatus());
        platform.setRedirectUri(dto.getRedirectUrl());
        platform.setUpdateBy(SecurityUtils.getUserName());
        platform.setUpdateTime(DateUtils.getNowDate());
        platform.setRemark(dto.getRemark());

        oauthPlatformMapper.updateById(platform);

        // 3. 获取平台标签用于日志
        String platformLabel = sysDictApi.getDictLabel("oauth_platform", dto.getPlatform());
        String displayName = platformLabel != null ? platformLabel : dto.getPlatform();

        // 4. 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.withRawData(
                "update",
                "oauth_config",
                "更新了OAuth配置「" + displayName + "」",
                String.valueOf(platform.getId()),
                displayName,
                JsonUtils.toJsonString(dto)
        ));
    }

    /**
     * 删除 Oauth 配置
     * @param id 配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOauthConfig(Long id) {
        // 1. 查询配置信息
        OauthPlatform platform = oauthPlatformMapper.selectById(id);
        if (platform == null) {
            throw new RuntimeException("OAuth配置不存在");
        }

        // 2. 检查是否为系统内置配置
        if (Boolean.TRUE.equals(platform.getIsSystem())) {
            throw new RuntimeException("系统内置配置不允许删除");
        }

        // 3. 删除平台配置
        oauthPlatformMapper.deleteById(id);

        // 4. 获取平台标签用于日志
        String platformLabel = sysDictApi.getDictLabel("oauth_platform", platform.getPlatform());
        String displayName = platformLabel != null ? platformLabel : platform.getPlatform();

        // 5. 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of(
                "delete",
                "oauth_config",
                "删除了OAuth配置「" + displayName + "」",
                String.valueOf(id),
                displayName
        ));
    }

    /**
     * 批量删除 Oauth 配置
     * @param ids 配置ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOauthConfigBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // 1. 查询所有配置
        List<OauthPlatform> platforms = oauthPlatformMapper.selectBatchIds(ids);

        // 2. 检查是否包含系统内置配置
        boolean hasSystemConfig = platforms.stream()
                .anyMatch(p -> Boolean.TRUE.equals(p.getIsSystem()));
        if (hasSystemConfig) {
            throw new RuntimeException("批量删除中包含系统内置配置，不允许删除");
        }

        // 3. 批量删除
        oauthPlatformMapper.deleteBatchIds(ids);

        // 4. 发布操作日志事件
        EventBus.get().callEvent(UserOperationEvent.of(
                "delete",
                "oauth_config",
                "批量删除了 " + ids.size() + " 个OAuth配置",
                null,
                null
        ));
    }
}