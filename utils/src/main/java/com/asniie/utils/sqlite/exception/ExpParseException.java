package com.asniie.utils.sqlite.exception;

/*
 * Created by XiaoWei on 2019/1/9.
 */
public final class ExpParseException extends RuntimeException {
    public ExpParseException() {
        super();
    }

    public ExpParseException(String message) {
        super(message);
    }

    public ExpParseException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExpParseException(Throwable cause) {
        super(cause);
    }

}
