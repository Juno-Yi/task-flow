package com.junoyi.framework.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 文件存储类型枚举
 *
 * @author Fan
 */
@Getter
@AllArgsConstructor
public enum StorageType {

    /**
     * 本地存储
     */
    LOCAL("local", "本地存储"),

    /**
     * 阿里云 OSS
     */
    ALIYUN_OSS("aliyun-oss", "阿里云OSS"),

    /**
     * MinIO 对象存储
     */
    MINIO("minio", "MinIO对象存储"),

    /**
     * 七牛云存储
     */
    QINIU("qiniu", "七牛云存储"),

    /**
     * 腾讯云 COS
     */
    TENCENT_COS("tencent-cos", "腾讯云COS");

    /**
     * 存储类型代码
     */
    private final String code;

    /**
     * 存储类型名称
     */
    private final String name;

    /**
     * 根据代码获取存储类型
     */
    public static StorageType fromCode(String code) {
        for (StorageType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("未知的存储类型: " + code);
    }
}
