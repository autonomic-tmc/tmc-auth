package com.autonomic.tmc.auth.exception;

public class SdkServiceException extends BaseSdkException {

    public SdkServiceException(ErrorSourceType errorSourceType, String message) {
        super(errorSourceType, message);
    }

    public SdkServiceException(ErrorSourceType errorSourceType, String message, Throwable cause) {
        super(errorSourceType, message, cause);
    }
}
