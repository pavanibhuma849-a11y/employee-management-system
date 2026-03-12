package com.company.ems.controller;

import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.service.IProjectService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectRequestDTO requestDTO;
    private ProjectResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Project Alpha");
        requestDTO.setDuration(6);
        requestDTO.setStartDate(LocalDate.now());
        requestDTO.setEndDate(LocalDate.now().plusMonths(6));

        responseDTO = new ProjectResponseDTO();
        responseDTO.setId(1L);
        responseDTO.setName("Project Alpha");
        responseDTO.setDuration(6);
    }

    @Test
    void testCreateProject() throws Exception {
        when(projectService.createProject(any(ProjectRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Project Alpha")));

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    void testAssignEmployeeToProject() throws Exception {
        doNothing().when(projectService).assignEmployeeToProject(1L, 1L);

        mockMvc.perform(post("/projects/1/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Employee assigned to project successfully")));

        verify(projectService, times(1)).assignEmployeeToProject(1L, 1L);
    }
}
