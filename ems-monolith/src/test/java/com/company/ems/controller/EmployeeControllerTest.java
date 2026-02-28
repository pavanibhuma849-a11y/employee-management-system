package com.company.ems.controller;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.service.IEmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

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
        employeeRequestDTO.setRole("Software Engineer");
        employeeRequestDTO.setSalary(75000.0);
        employeeRequestDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        employeeRequestDTO.setDepartmentId(1L);

        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setName("John Doe");
        employeeResponseDTO.setRole("Software Engineer");
        employeeResponseDTO.setSalary(75000.0);
        employeeResponseDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        employeeResponseDTO.setDepartmentName("Engineering");
    }

    @Test
    public void testCreateEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(employeeResponseDTO);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Software Engineer")))
                .andExpect(jsonPath("$.salary", is(75000.0)))
                .andExpect(jsonPath("$.departmentName", is("Engineering")));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    public void testGetEmployeeById_Success() throws Exception {
        when(employeeService.getEmployeeById(1L))
                .thenReturn(employeeResponseDTO);

        mockMvc.perform(get("/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.role", is("Software Engineer")))
                .andExpect(jsonPath("$.salary", is(75000.0)))
                .andExpect(jsonPath("$.departmentName", is("Engineering")));

        verify(employeeService, times(1)).getEmployeeById(1L);
    }

    @Test
    public void testGetEmployees_WithPagination() throws Exception {
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> page = new PageImpl<>(employees, PageRequest.of(0, 10), 1);

        when(employeeService.getEmployees(null, PageRequest.of(0, 10)))
                .thenReturn(page);

        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].id", is(1)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.totalElements", is(1)));

        verify(employeeService, times(1)).getEmployees(null, PageRequest.of(0, 10));
    }

    @Test
    public void testGetEmployees_WithDepartmentFilter() throws Exception {
        List<EmployeeResponseDTO> employees = Arrays.asList(employeeResponseDTO);
        Page<EmployeeResponseDTO> page = new PageImpl<>(employees, PageRequest.of(0, 10), 1);

        when(employeeService.getEmployees("Engineering", PageRequest.of(0, 10)))
                .thenReturn(page);

        mockMvc.perform(get("/employees")
                .param("department", "Engineering")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[0].departmentName", is("Engineering")));

        verify(employeeService, times(1)).getEmployees("Engineering", PageRequest.of(0, 10));
    }

    @Test
    public void testGetEmployees_EmptyPage() throws Exception {
        Page<EmployeeResponseDTO> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);

        when(employeeService.getEmployees(null, PageRequest.of(0, 10)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.totalElements", is(0)));

        verify(employeeService, times(1)).getEmployees(null, PageRequest.of(0, 10));
    }

    @Test
    public void testGetSortedEmployees_Success() throws Exception {
        EmployeeResponseDTO employee1 = new EmployeeResponseDTO();
        employee1.setId(1L);
        employee1.setName("Alice Johnson");
        employee1.setRole("Senior Engineer");
        employee1.setJoiningDate(LocalDate.of(2020, 5, 10));

        EmployeeResponseDTO employee2 = new EmployeeResponseDTO();
        employee2.setId(2L);
        employee2.setName("Bob Smith");
        employee2.setRole("Junior Engineer");
        employee2.setJoiningDate(LocalDate.of(2023, 3, 20));

        List<EmployeeResponseDTO> sortedEmployees = Arrays.asList(employee1, employee2);

        when(employeeService.getAllEmployeesSortedByNameAndDate())
                .thenReturn(sortedEmployees);

        mockMvc.perform(get("/employees/sorted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", is("Alice Johnson")))
                .andExpect(jsonPath("$[1].name", is("Bob Smith")));

        verify(employeeService, times(1)).getAllEmployeesSortedByNameAndDate();
    }

    @Test
    public void testGetSortedEmployees_Empty() throws Exception {
        when(employeeService.getAllEmployeesSortedByNameAndDate())
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/employees/sorted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(employeeService, times(1)).getAllEmployeesSortedByNameAndDate();
    }

    @Test
    public void testUpdateEmployee_Success() throws Exception {
        EmployeeResponseDTO updatedEmployee = new EmployeeResponseDTO();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Doe");
        updatedEmployee.setRole("Senior Software Engineer");
        updatedEmployee.setSalary(95000.0);
        updatedEmployee.setJoiningDate(LocalDate.of(2022, 1, 15));
        updatedEmployee.setDepartmentName("Engineering");

        EmployeeUpdateRequestDTO updateRequest = new EmployeeUpdateRequestDTO();
        updateRequest.setName("John Doe");
        updateRequest.setRole("Senior Software Engineer");
        updateRequest.setSalary(95000.0);
        updateRequest.setJoiningDate(LocalDate.of(2022, 1, 15));
        updateRequest.setDepartmentId(1L);

        when(employeeService.updateEmployee(eq(1L), any(EmployeeUpdateRequestDTO.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.role", is("Senior Software Engineer")))
                .andExpect(jsonPath("$.salary", is(95000.0)));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteEmployee_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(1L);
    }

    @Test
    public void testCreateEmployee_InvalidInput() throws Exception {
        EmployeeRequestDTO invalidRequest = new EmployeeRequestDTO();
        invalidRequest.setName("");

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetEmployees_WithMultipleRecords() throws Exception {
        EmployeeResponseDTO emp1 = new EmployeeResponseDTO();
        emp1.setId(1L);
        emp1.setName("John Doe");

        EmployeeResponseDTO emp2 = new EmployeeResponseDTO();
        emp2.setId(2L);
        emp2.setName("Jane Smith");

        EmployeeResponseDTO emp3 = new EmployeeResponseDTO();
        emp3.setId(3L);
        emp3.setName("Bob Johnson");

        List<EmployeeResponseDTO> employees = Arrays.asList(emp1, emp2, emp3);
        Page<EmployeeResponseDTO> page = new PageImpl<>(employees, PageRequest.of(0, 10), 3);

        when(employeeService.getEmployees(null, PageRequest.of(0, 10)))
                .thenReturn(page);

        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(3)))
                .andExpect(jsonPath("$.content[0].name", is("John Doe")))
                .andExpect(jsonPath("$.content[1].name", is("Jane Smith")))
                .andExpect(jsonPath("$.content[2].name", is("Bob Johnson")))
                .andExpect(jsonPath("$.totalElements", is(3)));

        verify(employeeService, times(1)).getEmployees(null, PageRequest.of(0, 10));
    }

    @Test
    public void testGetEmployeeById_NotFound() throws Exception {
        when(employeeService.getEmployeeById(999L))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: 999"));

        mockMvc.perform(get("/employees/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).getEmployeeById(999L);
    }

    @Test
    public void testCreateEmployee_ServiceException() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    public void testUpdateEmployee_NotFound() throws Exception {
        EmployeeUpdateRequestDTO updateRequest = new EmployeeUpdateRequestDTO();
        updateRequest.setName("John Doe");
        updateRequest.setRole("Senior Engineer");

        when(employeeService.updateEmployee(eq(999L), any(EmployeeUpdateRequestDTO.class)))
                .thenThrow(new EmployeeNotFoundException("Employee not found with id: 999"));

        mockMvc.perform(put("/employees/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).updateEmployee(eq(999L), any(EmployeeUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteEmployee_NotFound() throws Exception {
        doThrow(new EmployeeNotFoundException("Employee not found with id: 999"))
                .when(employeeService).deleteEmployee(999L);

        mockMvc.perform(delete("/employees/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(employeeService, times(1)).deleteEmployee(999L);
    }

    @Test
    public void testGetEmployees_ServiceException() throws Exception {
        when(employeeService.getEmployees(null, PageRequest.of(0, 10)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/employees")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).getEmployees(null, PageRequest.of(0, 10));
    }

    @Test
    public void testGetSortedEmployees_ServiceException() throws Exception {
        when(employeeService.getAllEmployeesSortedByNameAndDate())
                .thenThrow(new RuntimeException("Error fetching sorted employees"));

        mockMvc.perform(get("/employees/sorted")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(employeeService, times(1)).getAllEmployeesSortedByNameAndDate();
    }

    @Test
    public void testCreateEmployee_NullResponse() throws Exception {
        when(employeeService.createEmployee(any(EmployeeRequestDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post("/employees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(employeeRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(employeeService, times(1)).createEmployee(any(EmployeeRequestDTO.class));
    }

    @Test
    public void testGetEmployeeById_WithDifferentId() throws Exception {
        EmployeeResponseDTO employee2 = new EmployeeResponseDTO();
        employee2.setId(2L);
        employee2.setName("Jane Smith");
        employee2.setRole("Manager");
        employee2.setSalary(85000.0);

        when(employeeService.getEmployeeById(2L))
                .thenReturn(employee2);

        mockMvc.perform(get("/employees/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Jane Smith")))
                .andExpect(jsonPath("$.role", is("Manager")));

        verify(employeeService, times(1)).getEmployeeById(2L);
    }

    @Test
    public void testUpdateEmployee_WithNewDepartment() throws Exception {
        EmployeeResponseDTO updatedEmployee = new EmployeeResponseDTO();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Doe");
        updatedEmployee.setRole("Lead Engineer");
        updatedEmployee.setDepartmentName("R&D");

        EmployeeUpdateRequestDTO updateRequest = new EmployeeUpdateRequestDTO();
        updateRequest.setName("John Doe");
        updateRequest.setRole("Lead Engineer");
        updateRequest.setDepartmentId(2L);

        when(employeeService.updateEmployee(eq(1L), any(EmployeeUpdateRequestDTO.class)))
                .thenReturn(updatedEmployee);

        mockMvc.perform(put("/employees/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.departmentName", is("R&D")));

        verify(employeeService, times(1)).updateEmployee(eq(1L), any(EmployeeUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteEmployee_ThenNotFound() throws Exception {
        doNothing().when(employeeService).deleteEmployee(1L);

        mockMvc.perform(delete("/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(1L);
    }
}
