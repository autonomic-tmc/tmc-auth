package com.autonomic.tmc.auth.exception;

public class SdkClientException extends BaseSdkException {

    public SdkClientException(ErrorSourceType errorSourceType, String message) {
        super(errorSourceType, message);
    }

    public SdkClientException(ErrorSourceType errorSourceType, String message, Throwable cause) {
        super(errorSourceType, message, cause);
    }
}
