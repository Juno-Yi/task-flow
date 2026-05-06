package com.junoyi.framework.file.storage;

import com.junoyi.framework.file.domain.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * 文件存储接口
 * <p>
 * 定义文件存储的统一接口，支持多种存储实现
 *
 * @author Fan
 */
public interface FileStorage {

    /**
     * 上传文件
     *
     * @param file 文件
     * @param path 存储路径（相对路径）
     * @return 文件信息
     */
    FileInfo upload(MultipartFile file, String path);

    /**
     * 上传文件（流式）
     *
     * @param inputStream 输入流
     * @param fileName    文件名
     * @param path        存储路径
     * @param contentType 文件类型
     * @return 文件信息
     */
    FileInfo upload(InputStream inputStream, String fileName, String path, String contentType);

    /**
     * 下载文件
     *
     * @param filePath 文件路径
     * @return 文件字节数组
     */
    byte[] download(String filePath);

    /**
     * 删除文件
     *
     * @param filePath 文件路径
     * @return 是否删除成功
     */
    boolean delete(String filePath);

    /**
     * 文件是否存在
     *
     * @param filePath 文件路径
     * @return 是否存在
     */
    boolean exists(String filePath);

    /**
     * 获取文件访问URL
     *
     * @param filePath 文件路径
     * @return 访问URL
     */
    String getFileUrl(String filePath);

    /**
     * 获取文件访问URL（带过期时间）
     *
     * @param filePath 文件路径
     * @param expireSeconds 过期时间（秒）
     * @return 访问URL
     */
    String getFileUrl(String filePath, long expireSeconds);
}
