package com.epam.esm.exception;

import com.epam.esm.validator.ValidationError;

import java.util.EnumSet;

public class InvalidEntityException extends Exception {
    private EnumSet<ValidationError> validationErrors;
    private Object causeEntity;

    public InvalidEntityException(EnumSet<ValidationError> validationErrors, Object causeEntity) {
        this.validationErrors = validationErrors;
        this.causeEntity = causeEntity;
    }

    public EnumSet<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public Object getCauseEntity() {
        return causeEntity;
    }
}
