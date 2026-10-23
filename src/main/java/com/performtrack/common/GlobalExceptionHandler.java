package com.performtrack.common;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException ex) {

        return buildResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                Map.of());
    }

    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ApiError> handleBusinessRule(
            BusinessRuleException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                Map.of());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage())
        );

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Validation failed.",
                errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleInvalidBody(
            HttpMessageNotReadableException ex) {

        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "Invalid request body or date format.",
                Map.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDatabaseConflict(
            DataIntegrityViolationException ex) {

        return buildResponse(
                HttpStatus.CONFLICT,
                "The request conflicts with existing database data.",
                Map.of());
    }

    private ResponseEntity<ApiError> buildResponse(
            HttpStatus status,
            String message,
            Map<String, String> errors) {

        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                errors);

        return ResponseEntity.status(status).body(body);
    }
}
