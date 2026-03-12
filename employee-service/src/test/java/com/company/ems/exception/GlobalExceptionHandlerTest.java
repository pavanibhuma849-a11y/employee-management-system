package com.company.ems.exception;

import com.company.ems.dto.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.http.HttpMethod;

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

    @Test
    public void testHandleNoResourceFoundException() {
        String message = "No static resource employees .";
        NoResourceFoundException exception = new NoResourceFoundException(HttpMethod.POST, "employees ");

        ResponseEntity<ApiResponse<Object>> response = globalExceptionHandler.handleNotFoundException(exception, webRequest);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());

        ApiResponse<Object> body = response.getBody();
        assertTrue(body.getMessage().contains("No static resource employees "));
        assertEquals(HttpStatus.NOT_FOUND.value(), body.getStatus());
    }
}
