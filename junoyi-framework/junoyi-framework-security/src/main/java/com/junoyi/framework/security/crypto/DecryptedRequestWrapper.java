package com.junoyi.framework.security.crypto;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;

/**
 * 解密请求包装器
 * 用于包装解密后的请求体，使其可以被多次读取
 *
 * @author Fan
 */
public class DecryptedRequestWrapper extends HttpServletRequestWrapper {

    private static final String APPLICATION_JSON = "application/json;charset=UTF-8";
    private final byte[] body;

    /**
     * 构造函数，创建一个解密请求包装器
     * @param request 原始HTTP请求对象
     * @param decryptedBody 解密后的请求体字符串
     */
    public DecryptedRequestWrapper(HttpServletRequest request, String decryptedBody) {
        super(request);
        this.body = decryptedBody != null ? decryptedBody.getBytes(StandardCharsets.UTF_8) : new byte[0];
    }

    /**
     * 重写getContentType方法，返回JSON类型（解密后的内容是JSON格式）
     */
    @Override
    public String getContentType() {
        return APPLICATION_JSON;
    }

    /**
     * 重写getHeader方法，对Content-Type返回JSON类型
     */
    @Override
    public String getHeader(String name) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            return APPLICATION_JSON;
        }
        return super.getHeader(name);
    }

    /**
     * 重写getHeaders方法，对Content-Type返回JSON类型
     * Spring MVC 通过此方法获取 Content-Type
     */
    @Override
    public Enumeration<String> getHeaders(String name) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            return Collections.enumeration(Collections.singletonList(APPLICATION_JSON));
        }
        return super.getHeaders(name);
    }

    /**
     * 重写getInputStream方法，返回包含解密后数据的Servlet输入流
     * @return 包含解密数据的ServletInputStream对象
     * @throws IOException IO异常
     */
    @Override
    public ServletInputStream getInputStream() throws IOException {
        // 创建字节数组输入流来包装解密后的数据
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // 不需要实现
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    /**
     * 重写getReader方法，返回包含解密后数据的BufferedReader
     * @return 包含解密数据的BufferedReader对象
     * @throws IOException IO异常
     */
    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(getInputStream(), StandardCharsets.UTF_8));
    }

    /**
     * 重写getContentLength方法，返回解密后数据的长度
     * @return 解密数据的字节长度
     */
    @Override
    public int getContentLength() {
        return body.length;
    }

    /**
     * 重写getContentLengthLong方法，返回解密后数据的长度
     * @return 解密数据的字节长度
     */
    @Override
    public long getContentLengthLong() {
        return body.length;
    }

    /**
     * 获取解密后的请求体
     * @return 解密后的请求体字符串
     */
    public String getBody() {
        return new String(body, StandardCharsets.UTF_8);
    }
}

