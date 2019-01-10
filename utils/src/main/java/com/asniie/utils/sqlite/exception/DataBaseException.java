package com.asniie.utils.sqlite.exception;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public final class DataBaseException extends RuntimeException {
    public DataBaseException() {
        super();
    }

    public DataBaseException(String message) {
        super(message);
    }

    public DataBaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataBaseException(Throwable cause) {
        super(cause);
    }

}
