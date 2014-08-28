package com.ejushang.steward.common.exception;

/**
 * ERP项目业务异常
 * User: liubin
 * Date: 14-1-10
 */
public class SfBusinessException extends RuntimeException {

    public SfBusinessException() {
        super();
    }

    public SfBusinessException(String message) {
        super(message);
    }

    public SfBusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public SfBusinessException(Throwable cause) {
        super(cause);
    }

}
