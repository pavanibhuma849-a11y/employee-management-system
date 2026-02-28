package com.company.ems.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> handleNotFoundException(RuntimeException ex, WebRequest request) {
        try {
            logger.warn("Resource not found: {}", ex.getMessage());
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("message", ex.getMessage());
            logger.debug("Returning 404 response for not found exception");
            return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error handling not found exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(InvalidProjectDurationException.class)
    public ResponseEntity<?> handleBadRequestException(RuntimeException ex, WebRequest request) {
        try {
            logger.warn("Bad request: {}", ex.getMessage());
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("message", ex.getMessage());
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error handling bad request exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {
        try {
            logger.warn("Validation failed: {}", ex.getMessage());
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            
            Map<String, String> errors = ex.getBindingResult().getFieldErrors().stream()
                    .collect(Collectors.toMap(
                            error -> error.getField(),
                            error -> error.getDefaultMessage(),
                            (existing, replacement) -> existing
                    ));
            
            body.put("errors", errors);
            body.put("message", "Validation failed");
            
            return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error handling validation exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        try {
            logger.error("Unhandled exception occurred: {} - {}", ex.getClass().getName(), ex.getMessage(), ex);
            Map<String, Object> body = new HashMap<>();
            body.put("timestamp", LocalDateTime.now());
            body.put("message", ex.getMessage());
            logger.debug("Returning 500 response for global exception");
            return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Error handling global exception: {}", e.getMessage(), e);
            throw e;
        }
    }
}
