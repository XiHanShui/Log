package com.socct.mylibrary.exception;

/**
 * 日志追踪异常处理
 *
 * @author WJ
 * @date 19-4-19
 */
public class LogTrackerException extends RuntimeException {

    public LogTrackerException() {
        super();
    }

    public LogTrackerException(String message) {
        super(message);
    }
}
