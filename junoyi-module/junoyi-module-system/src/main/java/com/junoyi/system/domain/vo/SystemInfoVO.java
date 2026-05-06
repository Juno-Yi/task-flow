package com.junoyi.system.domain.vo;

import lombok.Builder;
import lombok.Data;

/**
 * 系统信息Vo
 *
 * @author Fan
 */
@Data
@Builder
public class SystemInfoVO {

    /**
     * 系统名
     */
    private String name;

    /**
     * 系统版本
     */
    private String version;

    /**
     * 版权年份
     */
    private String copyrightYear;

    /**
     * 版权信息
     */
    private String copyright;

    /**
     * 备案号信息
     */
    private String registration;

    /**
     * 系统logo
     */
    private String logo;
}
