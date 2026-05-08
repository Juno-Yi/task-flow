package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.junoyi.framework.core.domain.page.PageResult;
import com.junoyi.framework.core.utils.StringUtils;
import com.junoyi.oauth.convert.OauthConfigConverter;
import com.junoyi.oauth.domain.dto.OauthConfigQueryDTO;
import com.junoyi.oauth.domain.po.OauthPlatform;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.mapper.OauthPlatformMapper;
import com.junoyi.oauth.service.IOauthConfigService;
import com.junoyi.system.api.SysDictApi;
import com.junoyi.system.domain.vo.SysDictDataVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
    private final SysDictApi sysDictApi;

    /**
     * 分页查询Oauth平台配置
     * @param queryDTO 查询条件
     * @return Oauth平台配置分页结果
     */
    @Override
    public PageResult<OauthConfigVO> getOauthConfigList(OauthConfigQueryDTO queryDTO) {
        // 构建查询条件
        LambdaQueryWrapper<OauthPlatform> wrapper = new LambdaQueryWrapper<>();

        // 平台精确查询
        wrapper.eq(StringUtils.isNotBlank(queryDTO.getPlatform()),
                   OauthPlatform::getPlatform, queryDTO.getPlatform());

        // 状态查询
        wrapper.eq(queryDTO.getStatus() != null,
                   OauthPlatform::getStatus, queryDTO.getStatus());

        // 排序
        wrapper.orderByAsc(OauthPlatform::getId);

        // 分页查询
        Page<OauthPlatform> page = new Page<>(queryDTO.getPageNum(), queryDTO.getPageSize());
        Page<OauthPlatform> pageResult = oauthPlatformMapper.selectPage(page, wrapper);

        // 转换为VO并填充额外信息
        List<OauthConfigVO> voList = pageResult.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());

        // 如果有平台名称模糊查询，需要在内存中过滤
        if (StringUtils.isNotBlank(queryDTO.getPlatformName())) {
            voList = voList.stream()
                    .filter(vo -> vo.getPlatformLabel() != null &&
                                  vo.getPlatformLabel().contains(queryDTO.getPlatformName()))
                    .collect(Collectors.toList());
        }

        return new PageResult<>(voList, pageResult.getTotal());
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
}