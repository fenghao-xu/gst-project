package com.ylzs.common.util;

/**
 * 说明：专用于校验的异常
 *
 * @author Administrator
 * 2019-10-11 10:08
 */
public class AssertException extends RuntimeException {
    private static final long serialVersionUID = 6443115822777146153L;

    private final String message;

    public AssertException(String message) {
        super(message);
        this.message = message;
    }

    public AssertException(Throwable cause, String message) {
        super(cause);
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
