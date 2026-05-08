package com.junoyi.oauth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.junoyi.oauth.convert.OauthConfigConverter;
import com.junoyi.oauth.domain.po.OauthPlatform;
import com.junoyi.oauth.domain.vo.OauthConfigVO;
import com.junoyi.oauth.enums.OauthPlatformStatus;
import com.junoyi.oauth.enums.OauthPlatformType;
import com.junoyi.oauth.mapper.OauthPlatformMapper;
import com.junoyi.oauth.service.IOauthConfigService;
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

    /**
     * 获取Oauth平台配置
     * @return Oauth平台配置列表
     */
    @Override
    public List<OauthConfigVO> getOauthConfigList() {
        // 查询所有OAuth平台配置
        LambdaQueryWrapper<OauthPlatform> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(OauthPlatform::getId);

        List<OauthPlatform> platformList = oauthPlatformMapper.selectList(wrapper);

        // 转换为VO并填充额外信息
        return platformList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换PO为VO并填充额外信息
     */
    private OauthConfigVO convertToVO(OauthPlatform platform) {
        OauthConfigVO vo = OauthConfigConverter.INSTANCE.toVO(platform);

        // 填充平台标签
        vo.setPlatformLabel(OauthPlatformType.getLabel(platform.getPlatform()));

        // 填充状态信息
        OauthPlatformStatus status = OauthPlatformStatus.fromValue(platform.getStatus());
        if (status != null) {
            vo.setStatusLabel(status.getLabel());
            vo.setStatusType(status.getType());
        }

        return vo;
    }
}