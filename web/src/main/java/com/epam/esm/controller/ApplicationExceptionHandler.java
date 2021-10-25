package com.epam.esm.controller;

import com.epam.esm.exception.EmptyOrderException;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.exception.InvalidPageContextException;
import com.epam.esm.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private static final Logger applicationLogger = LogManager.getLogger();

    private static final String ERROR_MESSAGE = "errorMessage";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "resource_not_found";
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "entity_already_exists";
    private static final String ENTITY_NOT_FOUND_MESSAGE = "entity_not_found";

    // not found error when id is not passed to exception
    private static final String ENTITY_NOT_FOUND_WITHOUT_ID_MESSAGE = "entity_not_found_without_id";

    private static final String INVALID_ENTITY_MESSAGE = "invalid_entity";
    private static final String INVALID_NAME_MESSAGE = "invalid_entity.name";
    private static final String INVALID_DESCRIPTION_MESSAGE = "invalid_entity.description";
    private static final String INVALID_PRICE_MESSAGE = "invalid_entity.price";
    private static final String INVALID_DURATION_MESSAGE = "invalid_entity.duration";
    private static final String INVALID_PAGE_NUMBER_MESSAGE = "invalid_page_number";
    private static final String INVALID_PAGE_SIZE_MESSAGE = "invalid_page_size";
    private static final String EMPTY_ORDER_MESSAGE = "empty_order";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal_server_error";

    private static final String ERROR_SEPARATOR = ", ";

    private ResourceBundleMessageSource messageSource;

    public ApplicationExceptionHandler(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFoundException() {
        String errorMessage = getErrorMessage(RESOURCE_NOT_FOUND_MESSAGE);
        return buildErrorResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists() {
        String errorMessage = getErrorMessage(ENTITY_ALREADY_EXISTS_MESSAGE);
        return buildErrorResponseEntity(CONFLICT, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        String entityName = e.getCauseEntity().getSimpleName();
        Long entityId = e.getEntityId();
        String errorMessage = entityId != null
                ? String.format(getErrorMessage(ENTITY_NOT_FOUND_MESSAGE), entityName, entityId)
                : String.format(getErrorMessage(ENTITY_NOT_FOUND_WITHOUT_ID_MESSAGE), entityName);

        return buildErrorResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<Object> handleInvalidEntity(InvalidEntityException e) {
        Iterator<ValidationError> iterator = e.getValidationErrors().iterator();
        StringBuilder errorDetails = new StringBuilder();

        while (iterator.hasNext()) {
            ValidationError error = iterator.next();
            errorDetails.append(switch (error) {
                case INVALID_NAME -> getErrorMessage(INVALID_NAME_MESSAGE);
                case INVALID_DESCRIPTION -> getErrorMessage(INVALID_DESCRIPTION_MESSAGE);
                case INVALID_PRICE -> getErrorMessage(INVALID_PRICE_MESSAGE);
                case INVALID_DURATION -> getErrorMessage(INVALID_DURATION_MESSAGE);
            });

            if (iterator.hasNext()) {
                errorDetails.append(ERROR_SEPARATOR);
            }
        }

        String entityName = e.getCauseEntity().getSimpleName();
        String errorMessage = String.format(getErrorMessage(INVALID_ENTITY_MESSAGE), entityName, errorDetails);
        return buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(InvalidPageContextException.class)
    public ResponseEntity<Object> handleInvalidPageContext(InvalidPageContextException e) {
        InvalidPageContextException.ErrorType errorType = e.getErrorType();
        int invalidValue = e.getInvalidValue();

        String errorMessage = switch (errorType) {
            case INVALID_PAGE_NUMBER -> getErrorMessage(INVALID_PAGE_NUMBER_MESSAGE);
            case INVALID_PAGE_SIZE -> getErrorMessage(INVALID_PAGE_SIZE_MESSAGE);
        };

        return buildErrorResponseEntity(BAD_REQUEST, String.format(errorMessage, invalidValue));
    }

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<Object> handleEmptyOrder() {
        String errorMessage = getErrorMessage(EMPTY_ORDER_MESSAGE);
        return buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception e) {
        applicationLogger.error("Uncaught exception", e);
        String errorMessage = getErrorMessage(INTERNAL_SERVER_ERROR_MESSAGE);
        return buildErrorResponseEntity(INTERNAL_SERVER_ERROR, errorMessage);
    }

    private String getErrorMessage(String errorMessageName) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(errorMessageName, null, locale);
    }

    private ResponseEntity<Object> buildErrorResponseEntity(HttpStatus status, String errorMessage) {
        Map<String, Object> body = new HashMap<>();
        body.put(ERROR_MESSAGE, errorMessage);

        return new ResponseEntity<>(body, status);
    }
}
