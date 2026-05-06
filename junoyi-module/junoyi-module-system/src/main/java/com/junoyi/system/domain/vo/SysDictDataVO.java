package com.junoyi.system.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据VO
 *
 * @author Fan
 */
@Data
public class SysDictDataVO {

    /**
     * 字典编码
     */
    @JsonProperty("dictCode")
    private Long dictCode;

    /**
     * 字典排序
     */
    @JsonProperty("dictSort")
    private Integer dictSort;

    /**
     * 字典标签
     */
    @JsonProperty("dictLabel")
    private String dictLabel;

    /**
     * 字典键值
     */
    @JsonProperty("dictValue")
    private String dictValue;

    /**
     * 字典类型
     */
    @JsonProperty("dictType")
    private String dictType;

    /**
     * 样式属性
     */
    @JsonProperty("cssClass")
    private String cssClass;

    /**
     * 表格回显样式
     */
    @JsonProperty("listClass")
    private String listClass;

    /**
     * 是否默认（Y是 N否）
     */
    @JsonProperty("isDefault")
    private String isDefault;

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
