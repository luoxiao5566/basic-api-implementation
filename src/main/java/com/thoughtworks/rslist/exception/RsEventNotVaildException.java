package com.thoughtworks.rslist.exception;

public class RsEventNotVaildException extends RuntimeException{
    private String errorMessage;

    public RsEventNotVaildException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getMessage() {
        return errorMessage;
    }
}
