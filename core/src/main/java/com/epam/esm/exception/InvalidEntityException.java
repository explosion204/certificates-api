package com.epam.esm.exception;

import com.epam.esm.validator.ValidationError;

import java.util.EnumSet;

public class InvalidEntityException extends Exception {
    private EnumSet<ValidationError> validationErrors;

    public InvalidEntityException(EnumSet<ValidationError> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public EnumSet<ValidationError> getValidationErrors() {
        return validationErrors;
    }
}
