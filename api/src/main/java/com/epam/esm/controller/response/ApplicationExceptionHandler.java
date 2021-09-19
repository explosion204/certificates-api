package com.epam.esm.controller.response;

import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Iterator;
import java.util.Locale;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ApplicationExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger logger = LogManager.getLogger();

    private static final String METHOD_NOT_ALLOWED_MESSAGE = "method_not_allowed";
    private static final String RESOURCE_NOT_FOUND_MESSAGE = "resource_not_found";
    private static final String INVALID_BODY_FORMAT_MESSAGE = "invalid_body_format";
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "entity_already_exists";
    private static final String ENTITY_NOT_FOUND_MESSAGE = "entity_not_found";
    private static final String INVALID_ENTITY_MESSAGE = "invalid_entity";
    private static final String INVALID_NAME = "invalid_entity.name";
    private static final String INVALID_DESCRIPTION = "invalid_entity.description";
    private static final String INVALID_PRICE = "invalid_entity.price";
    private static final String INVALID_DURATION = "invalid_entity.duration";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal_server_error";

    private static final String ERROR_SEPARATOR = ", ";

    private ResourceBundleMessageSource messageSource;

    public ApplicationExceptionHandler(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public ResponseEntity<Object> handleNoHandlerFoundException(
                NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntityFactory.createResponseEntity(NOT_FOUND, getErrorMessage(RESOURCE_NOT_FOUND_MESSAGE));
    }

    @Override
    public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                HttpHeaders headers, HttpStatus status, WebRequest request) {
        return ResponseEntityFactory.createResponseEntity(METHOD_NOT_ALLOWED, getErrorMessage(METHOD_NOT_ALLOWED_MESSAGE));
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers,
                HttpStatus status, WebRequest request) {
        return ResponseEntityFactory.createResponseEntity(BAD_REQUEST, getErrorMessage(INVALID_BODY_FORMAT_MESSAGE));
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists() {
        return ResponseEntityFactory.createResponseEntity(CONFLICT, getErrorMessage(ENTITY_ALREADY_EXISTS_MESSAGE));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        String errorMessage = String.format(getErrorMessage(ENTITY_NOT_FOUND_MESSAGE), e.getEntityId());
        return ResponseEntityFactory.createResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<Object> handleInvalidEntity(InvalidEntityException e) {
        Iterator<ValidationError> iterator = e.getValidationErrors().iterator();
        StringBuilder errorDetails = new StringBuilder();

        while (iterator.hasNext()) {
            ValidationError error = iterator.next();
            errorDetails.append(switch (error) {
                case NAME -> getErrorMessage(INVALID_NAME);
                case DESCRIPTION -> getErrorMessage(INVALID_DESCRIPTION);
                case PRICE -> getErrorMessage(INVALID_PRICE);
                case DURATION -> getErrorMessage(INVALID_DURATION);
            });

            if (iterator.hasNext()) {
                errorDetails.append(ERROR_SEPARATOR);
            }
        }

        Class<?> causeEntity = e.getCauseEntity();
        String errorMessage = String.format(getErrorMessage(INVALID_ENTITY_MESSAGE), causeEntity.getSimpleName(),
                errorDetails);
        return ResponseEntityFactory.createResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception e) {
        logger.error("Uncaught exception", e);
        return ResponseEntityFactory.createResponseEntity(INTERNAL_SERVER_ERROR,
                getErrorMessage(INTERNAL_SERVER_ERROR_MESSAGE));
    }

    private String getErrorMessage(String messageName) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(messageName, null, locale);
    }
}
