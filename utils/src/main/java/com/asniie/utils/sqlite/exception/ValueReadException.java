package com.asniie.utils.sqlite.exception;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public final class ValueReadException extends RuntimeException {
    public ValueReadException() {
        super();
    }

    public ValueReadException(String message) {
        super(message);
    }

    public ValueReadException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValueReadException(Throwable cause) {
        super(cause);
    }

}
