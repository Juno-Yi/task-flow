package com.junoyi.framework.core.domain.module;


import com.junoyi.framework.core.constant.HttpStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 统一响应对象
 *
 * @author Fan
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 成功状态码
     */
    public static final int SUCCESS = 200;

    /**
     * 失败状态码
     */
    public static final int FAIL = 500;


    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应消息
     */
    private String msg;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 构建统一响应结果
     *
     * @param data 响应数据
     * @param code 响应状态码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 统一响应对象
     */
    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }


    /**
     * 返回成功响应（无数据）
     *
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, "操作成功");
    }

    /**
     * 返回成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, "操作成功");
    }

    /**
     * 返回成功响应（自定义消息）
     *
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    /**
     * 返回成功响应（自定义消息和数据）
     *
     * @param msg  响应消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 成功响应对象
     */
    public static <T> R<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    /**
     * 返回失败响应（无数据）
     *
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail() {
        return restResult(null, FAIL, "操作失败");
    }

    /**
     * 返回失败响应（自定义消息）
     *
     * @param msg 响应消息
     * @param <T> 数据类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    /**
     * 返回失败响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, "操作失败");
    }

    /**
     * 返回失败响应（自定义消息和数据）
     *
     * @param msg  响应消息
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    /**
     * 返回失败响应（自定义状态码和消息）
     *
     * @param code 响应状态码
     * @param msg  响应消息
     * @param <T>  数据类型
     * @return 失败响应对象
     */
    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    /**
     * 返回警告消息
     *
     * @param msg 返回内容
     * @param <T> 数据类型
     * @return 警告消息
     */
    public static <T> R<T> warn(String msg) {
        return restResult(null, HttpStatus.WARN, msg);
    }

    /**
     * 返回警告消息
     *
     * @param msg  返回内容
     * @param data 数据对象
     * @param <T>  数据类型
     * @return 警告消息
     */
    public static <T> R<T> warn(String msg, T data) {
        return restResult(data, HttpStatus.WARN, msg);
    }

    /**
     * 判断是否为错误响应
     *
     * @param ret 响应对象
     * @param <T> 数据类型
     * @return 是否为错误响应
     */
    public static <T> Boolean isError(R<T> ret) {
        return !isSuccess(ret);
    }

    /**
     * 判断是否为成功响应
     *
     * @param ret 响应对象
     * @param <T> 数据类型
     * @return 是否为成功响应
     */
    public static <T> Boolean isSuccess(R<T> ret) {
        return R.SUCCESS == ret.getCode();
    }

}
