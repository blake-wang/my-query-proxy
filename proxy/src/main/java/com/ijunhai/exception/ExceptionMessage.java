package com.ijunhai.exception;

public class ExceptionMessage {
    private int code;
    private String error;
    private String exception;

    public ExceptionMessage(int code, String error, String exception) {
        this.code = code;
        this.error = error;
        this.exception = exception;
    }

    public int getCode() {
        return code;
    }

    public String getError() {
        return error;
    }

    public String getException() {
        return exception;
    }
}
