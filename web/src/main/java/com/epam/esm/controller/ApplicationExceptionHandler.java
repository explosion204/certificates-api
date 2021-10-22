package com.epam.esm.controller;

import com.epam.esm.exception.ApplicationAuthenticationException;
import com.epam.esm.exception.EmptyOrderException;
import com.epam.esm.exception.EntityAlreadyExistsException;
import com.epam.esm.exception.EntityNotFoundException;
import com.epam.esm.exception.InvalidEntityException;
import com.epam.esm.repository.exception.InvalidPageContextException;
import com.epam.esm.util.ResponseUtil;
import com.epam.esm.validator.ValidationError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.Iterator;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@RestControllerAdvice
public class ApplicationExceptionHandler {
    private static final Logger applicationLogger = LogManager.getLogger();

    private static final String RESOURCE_NOT_FOUND_MESSAGE = "resource_not_found";
    private static final String ENTITY_ALREADY_EXISTS_MESSAGE = "entity_already_exists";
    private static final String ENTITY_NOT_FOUND_MESSAGE = "entity_not_found";

    // not found error when id is not passed to exception
    private static final String ENTITY_NOT_FOUND_WITHOUT_ID_MESSAGE = "entity_not_found_without_id";

    private static final String ACCESS_DENIED_MESSAGE = "access_denied";
    private static final String INVALID_ENTITY_MESSAGE = "invalid_entity";
    private static final String INVALID_NAME_MESSAGE = "invalid_entity.name";
    private static final String INVALID_DESCRIPTION_MESSAGE = "invalid_entity.description";
    private static final String INVALID_PRICE_MESSAGE = "invalid_entity.price";
    private static final String INVALID_DURATION_MESSAGE = "invalid_entity.duration";
    private static final String INVALID_USERNAME_MESSAGE = "invalid_entity.invalid_username";
    private static final String INVALID_PASSWORD_MESSAGE = "invalid_entity.invalid_password";
    private static final String INVALID_PAGE_NUMBER_MESSAGE = "invalid_page_number";
    private static final String INVALID_PAGE_SIZE_MESSAGE = "invalid_page_size";
    private static final String EMPTY_ORDER_MESSAGE = "empty_order";
    private static final String INVALID_CREDENTIALS_MESSAGE = "invalid_credentials";
    private static final String INTERNAL_SERVER_ERROR_MESSAGE = "internal_server_error";

    private static final String ERROR_SEPARATOR = ", ";

    private ResponseUtil responseUtil;

    public ApplicationExceptionHandler(ResponseUtil responseUtil) {
        this.responseUtil = responseUtil;
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<Object> handleNoHandlerFound() {
        String errorMessage = responseUtil.getErrorMessage(RESOURCE_NOT_FOUND_MESSAGE);
        return responseUtil.buildErrorResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDenied() {
        String errorMessage = responseUtil.getErrorMessage(ACCESS_DENIED_MESSAGE);
        return responseUtil.buildErrorResponseEntity(FORBIDDEN, errorMessage);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    public ResponseEntity<Object> handleEntityAlreadyExists() {
        String errorMessage = responseUtil.getErrorMessage(ENTITY_ALREADY_EXISTS_MESSAGE);
        return responseUtil.buildErrorResponseEntity(CONFLICT, errorMessage);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Object> handleEntityNotFound(EntityNotFoundException e) {
        String entityName = e.getCauseEntity().getSimpleName();
        Long entityId = e.getEntityId();
        String errorMessage = entityId != null
                ? String.format(responseUtil.getErrorMessage(ENTITY_NOT_FOUND_MESSAGE), entityName, entityId)
                : String.format(responseUtil.getErrorMessage(ENTITY_NOT_FOUND_WITHOUT_ID_MESSAGE), entityName);

        return responseUtil.buildErrorResponseEntity(NOT_FOUND, errorMessage);
    }

    @ExceptionHandler(InvalidEntityException.class)
    public ResponseEntity<Object> handleInvalidEntity(InvalidEntityException e) {
        Iterator<ValidationError> iterator = e.getValidationErrors().iterator();
        StringBuilder errorDetails = new StringBuilder();

        while (iterator.hasNext()) {
            ValidationError error = iterator.next();
            errorDetails.append(switch (error) {
                case INVALID_NAME -> responseUtil.getErrorMessage(INVALID_NAME_MESSAGE);
                case INVALID_DESCRIPTION -> responseUtil.getErrorMessage(INVALID_DESCRIPTION_MESSAGE);
                case INVALID_PRICE -> responseUtil.getErrorMessage(INVALID_PRICE_MESSAGE);
                case INVALID_DURATION -> responseUtil.getErrorMessage(INVALID_DURATION_MESSAGE);
                case INVALID_USERNAME -> responseUtil.getErrorMessage(INVALID_USERNAME_MESSAGE);
                case INVALID_PASSWORD -> responseUtil.getErrorMessage(INVALID_PASSWORD_MESSAGE);
            });

            if (iterator.hasNext()) {
                errorDetails.append(ERROR_SEPARATOR);
            }
        }

        String entityName = e.getCauseEntity().getSimpleName();
        String errorMessage = String.format(responseUtil.getErrorMessage(INVALID_ENTITY_MESSAGE),
                entityName, errorDetails);
        return responseUtil.buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(InvalidPageContextException.class)
    public ResponseEntity<Object> handleInvalidPageContext(InvalidPageContextException e) {
        InvalidPageContextException.ErrorType errorType = e.getErrorType();
        int invalidValue = e.getInvalidValue();

        String errorMessage = switch (errorType) {
            case INVALID_PAGE_NUMBER -> responseUtil.getErrorMessage(INVALID_PAGE_NUMBER_MESSAGE);
            case INVALID_PAGE_SIZE -> responseUtil.getErrorMessage(INVALID_PAGE_SIZE_MESSAGE);
        };

        return responseUtil.buildErrorResponseEntity(BAD_REQUEST, String.format(errorMessage, invalidValue));
    }

    @ExceptionHandler(EmptyOrderException.class)
    public ResponseEntity<Object> handleEmptyOrder() {
        String errorMessage = responseUtil.getErrorMessage(EMPTY_ORDER_MESSAGE);
        return responseUtil.buildErrorResponseEntity(BAD_REQUEST, errorMessage);
    }

    @ExceptionHandler(ApplicationAuthenticationException.class)
    public ResponseEntity<Object> handleApplicationAuthenticationException(ApplicationAuthenticationException e) {
        ApplicationAuthenticationException.ErrorType errorType = e.getErrorType();

        String errorMessage = switch (errorType) {
            case INVALID_CREDENTIALS -> responseUtil.getErrorMessage(INVALID_CREDENTIALS_MESSAGE);
        };

        return responseUtil.buildErrorResponseEntity(UNAUTHORIZED, errorMessage);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleDefault(Exception e) {
        applicationLogger.error("Uncaught exception", e);
        String errorMessage = responseUtil.getErrorMessage(INTERNAL_SERVER_ERROR_MESSAGE);
        return responseUtil.buildErrorResponseEntity(INTERNAL_SERVER_ERROR, errorMessage);
    }
}
