package com.autonomic.tmc.auth.exception;

public class BaseSdkException extends RuntimeException {

    private final ErrorSourceType errorSourceType;

    public BaseSdkException(ErrorSourceType errorSourceType, String message) {
        super(message);
        this.errorSourceType = errorSourceType;
    }

    public BaseSdkException(ErrorSourceType errorSourceType, String message, Throwable cause) {
        super(message, cause);
        this.errorSourceType = errorSourceType;
    }
}
