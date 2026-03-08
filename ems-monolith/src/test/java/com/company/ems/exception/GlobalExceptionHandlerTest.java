package com.company.ems.exception;

import com.company.ems.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler;
    private ServletWebRequest webRequest;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
        MockHttpServletRequest request = new MockHttpServletRequest();
        webRequest = new ServletWebRequest(request);
    }

    @SuppressWarnings("unused")
    private void dummyMethod(String param) {}

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleEmployeeNotFoundException() {
        String message = "Employee not found with id: 1";
        EmployeeNotFoundException exception = new EmployeeNotFoundException(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleDepartmentNotFoundException() {
        String message = "Department not found with id: 5";
        DepartmentNotFoundException exception = new DepartmentNotFoundException(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleProjectNotFoundException() {
        String message = "Project not found with id: 10";
        ProjectNotFoundException exception = new ProjectNotFoundException(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleBadRequestException() {
        String message = "Invalid project duration";
        InvalidProjectDurationException exception = new InvalidProjectDurationException(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleBadRequestException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleValidationException() throws NoSuchMethodException {
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "field1", "must not be null"));
        bindingResult.addError(new FieldError("testObject", "field2", "must be positive"));

        MethodParameter methodParameter =
                new MethodParameter(this.getClass().getDeclaredMethod("dummyMethod", String.class), 0);
        MethodArgumentNotValidException exception =
                new MethodArgumentNotValidException(methodParameter, bindingResult);

        ResponseEntity<ApiResponse<Map<String, String>>> response = (ResponseEntity<ApiResponse<Map<String, String>>>) globalExceptionHandler.handleValidationException(exception);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ApiResponse<Map<String, String>> body = response.getBody();
        assertEquals("Validation failed", body.getMessage());

        Map<String, String> errors = body.getData();
        assertEquals(2, errors.size());
        assertEquals("must not be null", errors.get("field1"));
        assertEquals("must be positive", errors.get("field2"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleGlobalException() {
        String message = "An unexpected error occurred";
        Exception exception = new Exception(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleGlobalExceptionWithRuntimeException() {
        String message = "Runtime error";
        RuntimeException exception = new RuntimeException(message);

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertEquals(message, body.getMessage());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleNotFoundExceptionWithDifferentMessages() {
        String[] messages = {
            "Employee not found",
            "Department not found",
            "Project not found with specific criteria"
        };

        for (String message : messages) {
            EmployeeNotFoundException exception = new EmployeeNotFoundException(message);
            ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleNotFoundException(exception, webRequest);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            ApiResponse<Object> body = response.getBody();
            assertEquals(message, body.getMessage());
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleNotFoundExceptionResponseStructure() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Test message");

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleNotFoundException(exception, webRequest);
        ApiResponse<Object> body = response.getBody();

        assertNotNull(body.getMessage());
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testHandleGlobalExceptionResponseStructure() {
        Exception exception = new Exception("Test error");

        ResponseEntity<ApiResponse<Object>> response = (ResponseEntity<ApiResponse<Object>>) globalExceptionHandler.handleGlobalException(exception, webRequest);
        ApiResponse<Object> body = response.getBody();

        assertNotNull(body.getMessage());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.getStatus());
    }

    @Test
    public void testNotFoundExceptionStatusCode() {
        EmployeeNotFoundException exception = new EmployeeNotFoundException("Not found");
        ResponseEntity<?> response = globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    public void testGlobalExceptionStatusCode() {
        Exception exception = new Exception("Error");
        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(exception, webRequest);

        assertEquals(500, response.getStatusCode().value());
    }
}
