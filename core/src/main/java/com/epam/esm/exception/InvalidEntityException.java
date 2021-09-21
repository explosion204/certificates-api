package com.epam.esm.exception;

import com.epam.esm.validator.ValidationError;

import java.util.EnumSet;

public class InvalidEntityException extends RuntimeException {
    private EnumSet<ValidationError> validationErrors;
    private Class<?> causeEntity;

    public InvalidEntityException(EnumSet<ValidationError> validationErrors, Class<?> causeEntity) {
        this.validationErrors = validationErrors;
        this.causeEntity = causeEntity;
    }

    public EnumSet<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public Class<?> getCauseEntity() {
        return causeEntity;
    }
}
