package com.webtrak.user_service.exception;

import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Validation errors (DTO fields)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidationErrors(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors()
                .forEach(error ->
                        errors.put(error.getField(), error.getDefaultMessage())
                );

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        new ApiStatus(400, "VALIDATION_ERROR"),
                        "Invalid request data",
                        errors
                )
        );
    }

    // Business / input errors
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        new ApiStatus(400, "BAD_REQUEST"),
                        ex.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex
    ) {
        System.out.println(ex);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                new ApiResponse<>(
                        new ApiStatus(409, "CONFLICT"),
                        "Email or employee ID already exists",
                        null
                )
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(
            BadCredentialsException ex
    ) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                new ApiResponse<>(
                        new ApiStatus(401, "AUTH_FAILED"),
                        ex.getMessage(),
                        null
                )
        );
    }

    @ExceptionHandler(AccountNotActivatedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccountNotActivated(
            AccountNotActivatedException ex
    ) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                new ApiResponse<>(
                        new ApiStatus(403, "ACCOUNT_NOT_ACTIVATED"),
                        ex.getMessage(),
                        null
                )
        );
    }

    // Generic fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception ex) {

        ex.printStackTrace(); // acceptable for now

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ApiResponse<>(
                        new ApiStatus(500, "ERROR"),
                        "Internal server error",
                        null
                )
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex
    ) {
        String message = "Malformed request";

        Throwable cause = ex.getCause();
        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {

            if (ife.getTargetType().isEnum()) {
                Object[] enumValues = ife.getTargetType().getEnumConstants();

                message = "Invalid value for field '" +
                        ife.getPath().get(0).getFieldName() +
                        "'. Allowed values are: HR, EMPLOYEE";
            }
        }

        return ResponseEntity.badRequest().body(
                new ApiResponse<>(
                        new ApiStatus(400, "BAD_REQUEST"),
                        message,
                        null
                )
        );
    }
}
