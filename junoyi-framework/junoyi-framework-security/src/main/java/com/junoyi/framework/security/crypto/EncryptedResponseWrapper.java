package com.junoyi.framework.security.crypto;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

/**
 * 加密响应包装器
 * 用于捕获响应内容，以便后续进行加密处理
 *
 * @author Fan
 */
public class EncryptedResponseWrapper extends HttpServletResponseWrapper {

    private final ByteArrayOutputStream buffer;
    private ServletOutputStream outputStream;
    private PrintWriter writer;
    private final HttpServletResponse originalResponse;

    /**
     * 构造一个 EncryptedResponseWrapper 实例，用于包装原始的 HttpServletResponse。
     *
     * @param response 原始的 HttpServletResponse 对象
     */
    public EncryptedResponseWrapper(HttpServletResponse response) {
        super(response);
        this.originalResponse = response;
        this.buffer = new ByteArrayOutputStream();
    }

    /**
     * 获取用于写入响应数据的 ServletOutputStream。
     * <p>如果已经调用了 getWriter() 方法，则抛出 IllegalStateException。</p>
     *
     * @return 用于写入响应数据的 ServletOutputStream
     * @throws IOException 如果发生 IO 异常
     * @throws IllegalStateException 如果已经调用过 getWriter()
     */
    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null)
            throw new IllegalStateException("getWriter() has already been called");
        if (outputStream == null)
            outputStream = new CachedServletOutputStream(buffer);
        return outputStream;
    }

    /**
     * 获取用于写入字符数据的 PrintWriter。
     * <p>如果已经调用了 getOutputStream() 方法，则抛出 IllegalStateException。</p>
     *
     * @return 用于写入字符数据的 PrintWriter
     * @throws IOException 如果发生 IO 异常
     * @throws IllegalStateException 如果已经调用过 getOutputStream()
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        if (outputStream != null)
            throw new IllegalStateException("getOutputStream() has already been called");
        if (writer == null)
            writer = new PrintWriter(new OutputStreamWriter(buffer, StandardCharsets.UTF_8));
        return writer;
    }

    /**
     * 刷新缓冲区中的数据到输出流或 Writer 中。
     *
     * @throws IOException 如果发生 IO 异常
     */
    @Override
    public void flushBuffer() throws IOException {
        if (writer != null)
            writer.flush();
        if (outputStream != null)
            outputStream.flush();
    }

    /**
     * 获取捕获的响应内容（字符串形式）。
     * <p>在获取之前会先刷新缓冲区以确保所有数据都已写入。</p>
     *
     * @return 捕获的响应内容字符串
     * @throws IOException 如果发生 IO 异常
     */
    public String getCapturedContent() throws IOException {
        flushBuffer();
        return buffer.toString(StandardCharsets.UTF_8);
    }

    /**
     * 获取捕获的响应内容（字节数组形式）。
     * <p>在获取之前会先刷新缓冲区以确保所有数据都已写入。</p>
     *
     * @return 捕获的响应内容字节数组
     * @throws IOException 如果发生 IO 异常
     */
    public byte[] getCapturedBytes() throws IOException {
        flushBuffer();
        return buffer.toByteArray();
    }

    /**
     * 将加密后的内容写入原始响应中，并设置相关响应头信息。
     *
     * @param encryptedContent 加密后的响应内容字符串
     * @throws IOException 如果发生 IO 异常
     */
    public void writeEncryptedContent(String encryptedContent) throws IOException {
        byte[] encryptedBytes = encryptedContent.getBytes(StandardCharsets.UTF_8);
        originalResponse.setContentLength(encryptedBytes.length);
        originalResponse.setContentType("application/json;charset=UTF-8");
        originalResponse.setHeader("X-Encrypted", "true");
        originalResponse.getOutputStream().write(encryptedBytes);
        originalResponse.getOutputStream().flush();
    }

    /**
     * 自定义的 ServletOutputStream 实现，将输出缓存到内存中以便后续处理。
     */
    private static class CachedServletOutputStream extends ServletOutputStream {
        private final ByteArrayOutputStream buffer;

        /**
         * 使用指定的 ByteArrayOutputStream 初始化 CachedServletOutputStream。
         *
         * @param buffer 用于缓存输出数据的 ByteArrayOutputStream
         */
        public CachedServletOutputStream(ByteArrayOutputStream buffer) {
            this.buffer = buffer;
        }

        /**
         * 检查输出流是否准备好接收数据。
         *
         * @return 总是返回 true，表示始终准备就绪
         */
        @Override
        public boolean isReady() {
            return true;
        }

        /**
         * 设置 WriteListener 监听器。当前实现为空，因为不需要异步支持。
         *
         * @param writeListener 要设置的 WriteListener（未使用）
         */
        @Override
        public void setWriteListener(WriteListener writeListener) {
            // 不需要实现
        }

        /**
         * 写入单个字节到内部缓冲区。
         *
         * @param b 要写入的字节
         * @throws IOException 如果发生 IO 异常
         */
        @Override
        public void write(int b) throws IOException {
            buffer.write(b);
        }

        /**
         * 将整个字节数组写入内部缓冲区。
         *
         * @param b 要写入的字节数组
         * @throws IOException 如果发生 IO 异常
         */
        @Override
        public void write(byte[] b) throws IOException {
            buffer.write(b);
        }

        /**
         * 将指定范围内的字节数组写入内部缓冲区。
         *
         * @param b   要写入的字节数组
         * @param off 开始位置偏移量
         * @param len 要写入的长度
         * @throws IOException 如果发生 IO 异常
         */
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            buffer.write(b, off, len);
        }
    }

}
