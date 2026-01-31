package com.webtrak.user_service.exception;

import com.webtrak.user_service.dto.response.ApiResponse;
import com.webtrak.user_service.dto.response.ApiStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 🔴 Generic runtime errors (fallback)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(
            RuntimeException ex
    ) {
        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(400, "ERROR"),
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 🔴 Illegal arguments (bad input)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(400, "ERROR"),
                ex.getMessage(),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }

    // 🔴 Any unexpected exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(
            Exception ex
    ) {
        ApiResponse<Object> response = new ApiResponse<>(
                new ApiStatus(500, "ERROR"),
                "Internal server error",
                null
        );

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }
}
