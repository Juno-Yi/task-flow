package com.junoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型VO
 *
 * @author Fan
 */
@Data
public class SysDictTypeVO {

    /**
     * 字典主键
     */
    @JsonProperty("dictId")
    private Long dictId;

    /**
     * 字典名称
     */
    @JsonProperty("dictName")
    private String dictName;

    /**
     * 字典类型
     */
    @JsonProperty("dictType")
    private String dictType;

    /**
     * 状态（0正常 1停用）
     */
    @JsonProperty("status")
    private String status;

    /**
     * 备注
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 创建时间
     */
    @JsonProperty("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @JsonProperty("updateTime")
    private LocalDateTime updateTime;
}
