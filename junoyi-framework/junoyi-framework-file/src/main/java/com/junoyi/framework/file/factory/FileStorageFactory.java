package com.junoyi.framework.file.factory;

import com.junoyi.framework.file.properties.FileStorageProperties;
import com.junoyi.framework.file.storage.FileStorage;
import lombok.RequiredArgsConstructor;

/**
 * 文件存储工厂
 * <p>
 * 提供当前配置的文件存储实例
 *
 * @author Fan
 */
@RequiredArgsConstructor
public class FileStorageFactory {

    private final FileStorageProperties properties;
    private final FileStorage fileStorage;

    /**
     * 获取当前配置的文件存储实例
     *
     * @return 文件存储实例
     */
    public FileStorage getStorage() {
        return fileStorage;
    }

    /**
     * 获取存储类型名称
     *
     * @return 存储类型名称
     */
    public String getStorageTypeName() {
        return properties.getStorageType().getName();
    }
}
