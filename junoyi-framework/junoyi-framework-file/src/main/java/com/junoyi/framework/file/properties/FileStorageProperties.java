package com.junoyi.framework.file.properties;

import com.junoyi.framework.file.enums.StorageType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 文件存储配置属性
 *
 * @author Fan
 */
@Data
@ConfigurationProperties(prefix = "junoyi.file")
public class FileStorageProperties {

    /**
     * 存储类型（默认本地存储）
     */
    private StorageType storageType = StorageType.LOCAL;

    /**
     * 文件访问域名（用于生成文件URL）
     */
    private String domain;

    /**
     * 本地存储配置
     */
    private LocalConfig local = new LocalConfig();

    /**
     * 阿里云OSS配置
     */
    private AliyunOssConfig aliyunOss = new AliyunOssConfig();

    /**
     * MinIO配置
     */
    private MinioConfig minio = new MinioConfig();

    /**
     * 七牛云配置
     */
    private QiniuConfig qiniu = new QiniuConfig();

    /**
     * 本地存储配置
     */
    @Data
    public static class LocalConfig {
        /**
         * 本地存储根路径
         */
        private String basePath = "./upload";

        /**
         * URL路径前缀
         */
        private String urlPrefix = "/files";
    }

    /**
     * 阿里云OSS配置
     */
    @Data
    public static class AliyunOssConfig {
        /**
         * 访问密钥ID
         */
        private String accessKeyId;

        /**
         * 访问密钥Secret
         */
        private String accessKeySecret;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 端点（地域节点）
         */
        private String endpoint;

        /**
         * 自定义域名
         */
        private String customDomain;
    }

    /**
     * MinIO配置
     */
    @Data
    public static class MinioConfig {
        /**
         * 端点地址
         */
        private String endpoint;

        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 密钥
         */
        private String secretKey;

        /**
         * Bucket名称
         */
        private String bucketName;
    }

    /**
     * 七牛云配置
     */
    @Data
    public static class QiniuConfig {
        /**
         * 访问密钥
         */
        private String accessKey;

        /**
         * 密钥
         */
        private String secretKey;

        /**
         * Bucket名称
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain;

        /**
         * 区域
         */
        private String region;
    }
}
