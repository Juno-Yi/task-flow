package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.oauth.domain.dto.OauthConfigDTO;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthConfig;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.exception.OauthException;
import com.junoyi.oauth.mapper.OauthConfigMapper;
import com.junoyi.oauth.service.IOauthConfigService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Oauth 配置业务实现
 *
 * @author Fan
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OauthConfigServiceImpl implements IOauthConfigService {

    private final OauthConfigMapper oauthConfigMapper;
    private final SysDictApi sysDictApi;

    /**
     * 字典类型常量
     */
    private static final String DICT_TYPE_OAUTH_PLATFORM = "oauth_platform";
    private static final String DICT_TYPE_OAUTH_STATUS = "oauth_status";

    /**
     * 分页查询Oauth平台配置
     * @param queryDTO 查询条件
     * @param page 分页对象
     * @return Oauth平台配置分页结果
     */
    @Override
    public PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO, Page<OauthConfig> page) {
        log.debug("分页查询OAuth配置列表, queryDTO: {}, page: {}/{}", queryDTO, page.getCurrent(), page.getSize());

        // 构建查询条件
        LambdaQueryWrapper<OauthConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(queryDTO.getPlatform()), OauthConfig::getPlatform, queryDTO.getPlatform())
                .eq(queryDTO.getStatus() != null, OauthConfig::getStatus, queryDTO.getStatus())
                .orderByDesc(OauthConfig::getCreateTime);

        // 分页查询
        Page<OauthConfig> resultPage = oauthConfigMapper.selectPage(page, wrapper);

        // 手动转换PO到VO
        List<OauthConfigVO> voList = new ArrayList<>();
        for (OauthConfig config : resultPage.getRecords()) {
            OauthConfigVO vo = convertToVO(config);

            // 使用字典API翻译平台和状态标签
            if (StringUtils.hasText(vo.getPlatform())) {
                SysDictDataVO platformDict = sysDictApi.getDictItem(DICT_TYPE_OAUTH_PLATFORM, vo.getPlatform());
                if (platformDict != null) {
                    vo.setPlatformLabel(platformDict.getDictLabel());
                }
            }
            if (vo.getStatus() != null) {
                SysDictDataVO statusDict = sysDictApi.getDictItem(DICT_TYPE_OAUTH_STATUS, String.valueOf(vo.getStatus()));
                if (statusDict != null) {
                    vo.setStatusLabel(statusDict.getDictLabel());
                    vo.setStatusType(statusDict.getListClass());
                }
            }

            voList.add(vo);
        }

        return PageResult.of(voList, resultPage.getTotal(), (int) resultPage.getCurrent(), (int) resultPage.getSize());
    }

    /**
     * 添加 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addOauthConfig(OauthConfigDTO dto) {
        log.debug("添加OAuth配置, dto: {}", dto);

        // 检查平台是否已存在
        LambdaQueryWrapper<OauthConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OauthConfig::getPlatform, dto.getPlatform());
        Long count = oauthConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new OauthException("该平台配置已存在");
        }

        // DTO转PO
        OauthConfig config = convertToEntity(dto);
        config.setIsSystem(false); // 新增的配置默认非系统内置

        // 插入数据库
        int rows = oauthConfigMapper.insert(config);
        if (rows <= 0) {
            throw new OauthException("添加OAuth配置失败");
        }

        log.info("添加OAuth配置成功, platform: {}", dto.getPlatform());
    }

    /**
     * 更新 Oauth 配置
     * @param dto 传输对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOauthConfig(OauthConfigDTO dto) {
        log.debug("更新OAuth配置, dto: {}", dto);

        if (dto.getId() == null) {
            throw new OauthException("配置ID不能为空");
        }

        // 检查配置是否存在
        OauthConfig existConfig = oauthConfigMapper.selectById(dto.getId());
        if (existConfig == null) {
            throw new OauthException("OAuth配置不存在");
        }

        // 检查是否为系统内置配置
        if (Boolean.TRUE.equals(existConfig.getIsSystem())) {
            throw new OauthException("系统内置配置不允许修改");
        }

        // 检查平台是否与其他配置重复
        LambdaQueryWrapper<OauthConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(OauthConfig::getPlatform, dto.getPlatform())
                .ne(OauthConfig::getId, dto.getId());
        Long count = oauthConfigMapper.selectCount(wrapper);
        if (count > 0) {
            throw new OauthException("该平台配置已存在");
        }

        // 更新配置 - 手动复制属性
        updateEntityFromDTO(dto, existConfig);
        int rows = oauthConfigMapper.updateById(existConfig);
        if (rows <= 0) {
            throw new OauthException("更新OAuth配置失败");
        }

        log.info("更新OAuth配置成功, id: {}, platform: {}", dto.getId(), dto.getPlatform());
    }

    /**
     * 删除 Oauth 配置
     * @param id 配置ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOauthConfig(Long id) {
        log.debug("删除OAuth配置, id: {}", id);

        if (id == null) {
            throw new OauthException("配置ID不能为空");
        }

        // 检查配置是否存在
        OauthConfig config = oauthConfigMapper.selectById(id);
        if (config == null) {
            throw new OauthException("OAuth配置不存在");
        }

        // 检查是否为系统内置配置
        if (Boolean.TRUE.equals(config.getIsSystem())) {
            throw new OauthException("系统内置配置不允许删除");
        }

        // 删除配置
        int rows = oauthConfigMapper.deleteById(id);
        if (rows <= 0) {
            throw new OauthException("删除OAuth配置失败");
        }

        log.info("删除OAuth配置成功, id: {}, platform: {}", id, config.getPlatform());
    }

    /**
     * 批量删除 Oauth 配置
     * @param ids 配置ID列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOauthConfigBatch(List<Long> ids) {
        log.debug("批量删除OAuth配置, ids: {}", ids);

        if (ids == null || ids.isEmpty()) {
            throw new OauthException("配置ID列表不能为空");
        }

        // 检查是否包含系统内置配置
        LambdaQueryWrapper<OauthConfig> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(OauthConfig::getId, ids)
                .eq(OauthConfig::getIsSystem, true);
        Long systemCount = oauthConfigMapper.selectCount(wrapper);
        if (systemCount > 0) {
            throw new OauthException("系统内置配置不允许删除");
        }

        // 批量删除
        int rows = oauthConfigMapper.delete(new LambdaQueryWrapper<OauthConfig>()
                .in(OauthConfig::getId, ids));
        if (rows <= 0) {
            throw new OauthException("批量删除OAuth配置失败");
        }

        log.info("批量删除OAuth配置成功, 删除数量: {}", rows);
    }

    /**
     * PO转VO
     * @param entity PO对象
     * @return VO对象
     */
    private OauthConfigVO convertToVO(OauthConfig entity) {
        if (entity == null) {
            return null;
        }
        OauthConfigVO vo = new OauthConfigVO();
        BeanUtils.copyProperties(entity, vo);
        // redirectUri -> redirectUrl
        vo.setRedirectUrl(entity.getRedirectUri());
        return vo;
    }

    /**
     * DTO转PO
     * @param dto DTO对象
     * @return PO对象
     */
    private OauthConfig convertToEntity(OauthConfigDTO dto) {
        if (dto == null) {
            return null;
        }
        OauthConfig entity = new OauthConfig();
        BeanUtils.copyProperties(dto, entity);
        // redirectUrl -> redirectUri
        entity.setRedirectUri(dto.getRedirectUrl());
        return entity;
    }

    /**
     * 用DTO更新PO
     * @param dto DTO对象
     * @param entity PO对象
     */
    private void updateEntityFromDTO(OauthConfigDTO dto, OauthConfig entity) {
        if (dto == null || entity == null) {
            return;
        }
        // 只更新非空字段
        if (dto.getPlatform() != null) {
            entity.setPlatform(dto.getPlatform());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        if (dto.getConfigKey() != null) {
            entity.setConfigKey(dto.getConfigKey());
        }
        if (dto.getConfigValue() != null) {
            entity.setConfigValue(dto.getConfigValue());
        }
        if (dto.getRedirectUrl() != null) {
            entity.setRedirectUri(dto.getRedirectUrl());
        }
        if (dto.getRemark() != null) {
            entity.setRemark(dto.getRemark());
        }
    }
}