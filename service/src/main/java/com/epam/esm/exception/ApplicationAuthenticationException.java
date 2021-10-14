package com.epam.esm.exception;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.AuthenticationException;

public class ApplicationAuthenticationException extends AuthenticationException {
    private final ErrorType errorType;

    public enum ErrorType {
        INVALID_CREDENTIALS
    }

    public ApplicationAuthenticationException(ErrorType errorType) {
        super(StringUtils.EMPTY);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
