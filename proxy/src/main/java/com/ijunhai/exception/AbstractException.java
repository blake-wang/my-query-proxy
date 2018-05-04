package com.ijunhai.exception;

public class AbstractException extends Exception {
    private Throwable e;

    public AbstractException(Throwable e) {
        this.e = e;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
