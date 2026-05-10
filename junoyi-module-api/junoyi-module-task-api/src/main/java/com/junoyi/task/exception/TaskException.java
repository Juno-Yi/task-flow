package com.junoyi.task.exception;

import com.junoyi.framework.core.constant.HttpStatus;
import com.junoyi.framework.core.domain.base.BaseException;

/**
 * 任务业务异常基类
 *
 * @author Fan
 */
public class TaskException extends BaseException {

    private static final long serialVersionUID = 1L;

    public TaskException(int code, String message, String domain) {
        super(code, message, domain);
    }

    public TaskException(int code, String message) {
        super(code, message, null);
    }

    public TaskException(String message) {
        super(HttpStatus.BAD_REQUEST, message, null);
    }

    @Override
    public String getDomainPrefix() {
        return "TASK";
    }
}

