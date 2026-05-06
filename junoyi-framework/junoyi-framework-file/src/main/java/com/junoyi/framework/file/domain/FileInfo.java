package com.junoyi.framework.file.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文件信息
 *
 * @author Fan
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfo {

    /**
     * 文件ID
     */
    private Long fileId;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 存储文件名（唯一）
     */
    private String storageName;

    /**
     * 文件路径
     */
    private String filePath;

    /**
     * 文件URL（访问地址）
     */
    private String fileUrl;

    /**
     * 文件大小（字节）
     */
    private Long fileSize;

    /**
     * 文件类型（MIME类型）
     */
    private String contentType;

    /**
     * 文件扩展名
     */
    private String extension;

    /**
     * 存储类型
     */
    private String storageType;

    /**
     * 文件MD5
     */
    private String md5;

    /**
     * 上传时间
     */
    private LocalDateTime uploadTime;

    /**
     * 上传用户ID
     */
    private Long uploadUserId;
}
