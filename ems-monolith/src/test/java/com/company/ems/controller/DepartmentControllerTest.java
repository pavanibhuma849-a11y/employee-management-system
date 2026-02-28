package com.company.ems.controller;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.service.IDepartmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DepartmentController.class)
public class DepartmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IDepartmentService departmentService;

    @Autowired
    private ObjectMapper objectMapper;

    private DepartmentRequestDTO departmentRequestDTO;
    private DepartmentResponseDTO departmentResponseDTO;

    @BeforeEach
    public void setUp() {
        departmentRequestDTO = new DepartmentRequestDTO();
        departmentRequestDTO.setName("IT");

        departmentResponseDTO = new DepartmentResponseDTO();
        departmentResponseDTO.setId(1L);
        departmentResponseDTO.setName("IT");
    }

    @Test
    public void testCreateDepartment_Success() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenReturn(departmentResponseDTO);

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("IT")));

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    public void testCreateDepartment_WithDifferentName() throws Exception {
        DepartmentRequestDTO hrRequest = new DepartmentRequestDTO();
        hrRequest.setName("HR");

        DepartmentResponseDTO hrResponse = new DepartmentResponseDTO();
        hrResponse.setId(2L);
        hrResponse.setName("HR");

        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenReturn(hrResponse);

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(hrRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("HR")));

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    public void testGetDepartmentById_Success() throws Exception {
        when(departmentService.getDepartmentById(1L))
                .thenReturn(departmentResponseDTO);

        mockMvc.perform(get("/departments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("IT")));

        verify(departmentService, times(1)).getDepartmentById(1L);
    }

    @Test
    public void testGetDepartmentById_DifferentDepartment() throws Exception {
        DepartmentResponseDTO finance = new DepartmentResponseDTO();
        finance.setId(3L);
        finance.setName("Finance");

        when(departmentService.getDepartmentById(3L))
                .thenReturn(finance);

        mockMvc.perform(get("/departments/3")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(3)))
                .andExpect(jsonPath("$.name", is("Finance")));

        verify(departmentService, times(1)).getDepartmentById(3L);
    }

    @Test
    public void testGetAllDepartments_Success() throws Exception {
        DepartmentResponseDTO dept1 = new DepartmentResponseDTO();
        dept1.setId(1L);
        dept1.setName("IT");

        DepartmentResponseDTO dept2 = new DepartmentResponseDTO();
        dept2.setId(2L);
        dept2.setName("HR");

        DepartmentResponseDTO dept3 = new DepartmentResponseDTO();
        dept3.setId(3L);
        dept3.setName("Finance");

        List<DepartmentResponseDTO> departments = Arrays.asList(dept1, dept2, dept3);

        when(departmentService.getAllDepartments())
                .thenReturn(departments);

        mockMvc.perform(get("/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("IT")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("HR")))
                .andExpect(jsonPath("$[2].id", is(3)))
                .andExpect(jsonPath("$[2].name", is("Finance")));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    public void testGetAllDepartments_Empty() throws Exception {
        when(departmentService.getAllDepartments())
                .thenReturn(Arrays.asList());

        mockMvc.perform(get("/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    public void testGetAllDepartments_SingleDepartment() throws Exception {
        List<DepartmentResponseDTO> departments = Arrays.asList(departmentResponseDTO);

        when(departmentService.getAllDepartments())
                .thenReturn(departments);

        mockMvc.perform(get("/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("IT")));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    public void testUpdateDepartment_Success() throws Exception {
        DepartmentUpdateRequestDTO updateRequest = new DepartmentUpdateRequestDTO();
        updateRequest.setName("Information Technology");

        DepartmentResponseDTO updatedDept = new DepartmentResponseDTO();
        updatedDept.setId(1L);
        updatedDept.setName("Information Technology");

        when(departmentService.updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class)))
                .thenReturn(updatedDept);

        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Information Technology")));

        verify(departmentService, times(1)).updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class));
    }

    @Test
    public void testUpdateDepartment_ChangeName() throws Exception {
        DepartmentUpdateRequestDTO updateRequest = new DepartmentUpdateRequestDTO();
        updateRequest.setName("IT Department");

        DepartmentResponseDTO updatedDept = new DepartmentResponseDTO();
        updatedDept.setId(1L);
        updatedDept.setName("IT Department");

        when(departmentService.updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class)))
                .thenReturn(updatedDept);

        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("IT Department")));

        verify(departmentService, times(1)).updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class));
    }

    @Test
    public void testUpdateDepartment_DifferentDepartment() throws Exception {
        DepartmentUpdateRequestDTO updateRequest = new DepartmentUpdateRequestDTO();
        updateRequest.setName("Human Resources");

        DepartmentResponseDTO updatedDept = new DepartmentResponseDTO();
        updatedDept.setId(2L);
        updatedDept.setName("Human Resources");

        when(departmentService.updateDepartment(eq(2L), any(DepartmentUpdateRequestDTO.class)))
                .thenReturn(updatedDept);

        mockMvc.perform(put("/departments/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Human Resources")));

        verify(departmentService, times(1)).updateDepartment(eq(2L), any(DepartmentUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteDepartment_Success() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/departments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    @Test
    public void testDeleteDepartment_DifferentId() throws Exception {
        doNothing().when(departmentService).deleteDepartment(2L);

        mockMvc.perform(delete("/departments/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(departmentService, times(1)).deleteDepartment(2L);
    }

    @Test
    public void testCreateDepartment_EmptyName() throws Exception {
        DepartmentRequestDTO emptyRequest = new DepartmentRequestDTO();
        emptyRequest.setName("");

        DepartmentResponseDTO emptyResponse = new DepartmentResponseDTO();
        emptyResponse.setId(1L);
        emptyResponse.setName("");

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetAllDepartments_MultipleDepartments() throws Exception {
        DepartmentResponseDTO dept1 = new DepartmentResponseDTO();
        dept1.setId(1L);
        dept1.setName("IT");

        DepartmentResponseDTO dept2 = new DepartmentResponseDTO();
        dept2.setId(2L);
        dept2.setName("HR");

        DepartmentResponseDTO dept3 = new DepartmentResponseDTO();
        dept3.setId(3L);
        dept3.setName("Finance");

        DepartmentResponseDTO dept4 = new DepartmentResponseDTO();
        dept4.setId(4L);
        dept4.setName("Operations");

        List<DepartmentResponseDTO> departments = Arrays.asList(dept1, dept2, dept3, dept4);

        when(departmentService.getAllDepartments())
                .thenReturn(departments);

        mockMvc.perform(get("/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].name", is("IT")))
                .andExpect(jsonPath("$[1].name", is("HR")))
                .andExpect(jsonPath("$[2].name", is("Finance")))
                .andExpect(jsonPath("$[3].name", is("Operations")));

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    public void testGetDepartmentById_NotFound() throws Exception {
        when(departmentService.getDepartmentById(999L))
                .thenThrow(new DepartmentNotFoundException("Department not found with id: 999"));

        mockMvc.perform(get("/departments/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).getDepartmentById(999L);
    }

    @Test
    public void testCreateDepartment_ServiceException() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    public void testUpdateDepartment_NotFound() throws Exception {
        DepartmentUpdateRequestDTO updateRequest = new DepartmentUpdateRequestDTO();
        updateRequest.setName("Updated Department");

        when(departmentService.updateDepartment(eq(999L), any(DepartmentUpdateRequestDTO.class)))
                .thenThrow(new DepartmentNotFoundException("Department not found with id: 999"));

        mockMvc.perform(put("/departments/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).updateDepartment(eq(999L), any(DepartmentUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteDepartment_NotFound() throws Exception {
        doThrow(new DepartmentNotFoundException("Department not found with id: 999"))
                .when(departmentService).deleteDepartment(999L);

        mockMvc.perform(delete("/departments/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(departmentService, times(1)).deleteDepartment(999L);
    }

    @Test
    public void testGetAllDepartments_ServiceException() throws Exception {
        when(departmentService.getAllDepartments())
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/departments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).getAllDepartments();
    }

    @Test
    public void testCreateDepartment_NullResponse() throws Exception {
        when(departmentService.createDepartment(any(DepartmentRequestDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post("/departments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(departmentRequestDTO)))
                .andExpect(status().isOk());

        verify(departmentService, times(1)).createDepartment(any(DepartmentRequestDTO.class));
    }

    @Test
    public void testUpdateDepartment_WithException() throws Exception {
        DepartmentUpdateRequestDTO updateRequest = new DepartmentUpdateRequestDTO();
        updateRequest.setName("Marketing");

        when(departmentService.updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/departments/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).updateDepartment(eq(1L), any(DepartmentUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteDepartment_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/departments/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(departmentService, times(1)).deleteDepartment(1L);
    }
}
