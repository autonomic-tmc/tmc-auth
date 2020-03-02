package com.autonomic.tmc.auth.exception;

public class SdkClientException extends BaseSdkException {

    private final ErrorSourceType errorSourceType;
    private final String message;

    public SdkClientException(ErrorSourceType errorSourceType, String message) {
        super(errorSourceType, message);
        this.errorSourceType = errorSourceType;
        this.message = message;
    }

    public SdkClientException(ErrorSourceType errorSourceType, String message, Throwable cause) {
        super(errorSourceType, message, cause);
        this.errorSourceType = errorSourceType;
        this.message = message;
    }

    @Override
    public String toString() {
        return "SdkClientException{" +
            "\nerrorSourceType=" + errorSourceType +
            "\nerrorMessage=" + message +
            '}';
    }
}
