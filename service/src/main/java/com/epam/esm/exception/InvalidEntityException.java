package com.epam.esm.exception;

import com.epam.esm.validator.ValidationError;

import java.util.EnumSet;
import java.util.List;

public class InvalidEntityException extends RuntimeException {
    private List<ValidationError> validationErrors;
    private Class<?> causeEntity;

    public InvalidEntityException(List<ValidationError> validationErrors, Class<?> causeEntity) {
        this.validationErrors = validationErrors;
        this.causeEntity = causeEntity;
    }

    public List<ValidationError> getValidationErrors() {
        return validationErrors;
    }

    public Class<?> getCauseEntity() {
        return causeEntity;
    }
}
