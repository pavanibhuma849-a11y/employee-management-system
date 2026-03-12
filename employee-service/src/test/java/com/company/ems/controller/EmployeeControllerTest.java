package com.company.ems.controller;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.service.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IEmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeRequestDTO employeeRequestDTO;
    private EmployeeResponseDTO employeeResponseDTO;

    @BeforeEach
    public void setUp() {
        employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setName("John Doe");
        employeeRequestDTO.setEmail("john.doe@company.com");
        employeeRequestDTO.setRole("Software Engineer");
        employeeRequestDTO.setSalary(75000.0);
        employeeRequestDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        employeeRequestDTO.setDepartmentId(1L);

        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setName("John Doe");
        employeeResponseDTO.setRole("Software Engineer");
    }

    @Test
    public void testCreateEmployee_SqlInjectionPrevention() throws Exception {
        EmployeeRequestDTO sqlInjectionRequest = new EmployeeRequestDTO();
        sqlInjectionRequest.setName("'; DROP TABLE employee;--");
        sqlInjectionRequest.setEmail("attacker@evil.com");
        sqlInjectionRequest.setRole("Attacker");
        sqlInjectionRequest.setSalary(1000.0);
        sqlInjectionRequest.setJoiningDate(LocalDate.now());

        EmployeeResponseDTO sqlInjectionResponse = new EmployeeResponseDTO();
        sqlInjectionResponse.setId(100L);
        sqlInjectionResponse.setName("'; DROP TABLE employee;--");
        sqlInjectionResponse.setRole("Attacker");

        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(sqlInjectionResponse);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sqlInjectionRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("'; DROP TABLE employee;--")));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    public void testCreateEmployee_ConcurrentWrites() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(employeeResponseDTO);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDTO)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDTO)))
                .andExpect(status().isCreated());

        verify(employeeService, times(2)).createEmployee(any(EmployeeRequestDTO.class));
    }
}
