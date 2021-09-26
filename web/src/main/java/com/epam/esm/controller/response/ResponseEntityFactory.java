package com.epam.esm.controller.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseEntityFactory {
    private static final String STATUS = "status";
    private static final String DATA = "data";
    private static final String ERROR_MESSAGE = "errorMessage";

    private ResponseEntityFactory() {

    }

    public static ResponseEntity<Object> createResponseEntity(HttpStatus status, Object data) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, status.value());
        body.put(DATA, data);

        return new ResponseEntity<>(body, status);
    }

    public static ResponseEntity<Object> createResponseEntity(HttpStatus status, String errorMessage) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, status.value());
        body.put(ERROR_MESSAGE, errorMessage);

        return new ResponseEntity<>(body, status);
    }

    public static ResponseEntity<Object> createResponseEntity(HttpStatus status) {
        Map<String, Object> body = new HashMap<>();
        body.put(STATUS, status.value());

        return new ResponseEntity<>(body, status);
    }
}
