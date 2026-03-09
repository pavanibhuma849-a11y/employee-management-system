package com.company.ems.exception;

import com.company.ems.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler({EmployeeNotFoundException.class, DepartmentNotFoundException.class, ProjectNotFoundException.class})
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(RuntimeException ex, WebRequest request) {
        try {
            logger.warn("Resource not found: {}", ex.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.NOT_FOUND.value(), null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error handling not found exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(InvalidProjectDurationException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(RuntimeException ex, WebRequest request) {
        try {
            logger.warn("Bad request: {}", ex.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error handling bad request exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        try {
            logger.warn("JSON parse error: {}", ex.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), null, "Invalid data type in request body");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error handling JSON parse exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(MethodArgumentNotValidException ex) {
        try {
            logger.warn("Validation failed: {}", ex.getMessage());
            
            Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage(),
                            (existing, replacement) -> existing
                    ));
            
            ApiResponse<Map<String, String>> response = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errors, "Validation failed");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error handling validation exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler({DuplicateResourceException.class, ResourceConflictException.class})
    public ResponseEntity<ApiResponse<Object>> handleConflictException(RuntimeException ex, WebRequest request) {
        try {
            logger.warn("Conflict: {}", ex.getMessage());
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.CONFLICT.value(), null, ex.getMessage());
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } catch (Exception e) {
            logger.error("Error handling conflict exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleDatabaseException(DataAccessException ex, WebRequest request) {
        try {
            logger.error("Database error occurred: {}", ex.getMessage(), ex);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "A database error occurred. Please try again later.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error handling database exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGlobalException(Exception ex, WebRequest request) {
        try {
            logger.error("Unhandled exception occurred: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
            ApiResponse<Object> response = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), null, "An internal server error occurred.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error handling global exception: {}", e.getMessage(), e);
            throw e;
        }
    }
}
