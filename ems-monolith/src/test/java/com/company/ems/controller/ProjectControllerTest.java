package com.company.ems.controller;

import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.service.IProjectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

@WebMvcTest(ProjectController.class)
public class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IProjectService projectService;

    @Autowired
    private ObjectMapper objectMapper;

    private ProjectRequestDTO projectRequestDTO;
    private ProjectResponseDTO projectResponseDTO;

    @BeforeEach
    public void setUp() {
        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setName("Mobile App Development");
        projectRequestDTO.setDuration(6);

        projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(1L);
        projectResponseDTO.setName("Mobile App Development");
        projectResponseDTO.setDuration(6);
    }

    @Test
    public void testCreateProject_Success() throws Exception {
        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenReturn(projectResponseDTO);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Mobile App Development")))
                .andExpect(jsonPath("$.data.duration", is(6)));

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testCreateProject_WithDifferentDuration() throws Exception {
        ProjectRequestDTO webRequest = new ProjectRequestDTO();
        webRequest.setName("Web Application");
        webRequest.setDuration(12);

        ProjectResponseDTO webResponse = new ProjectResponseDTO();
        webResponse.setId(2L);
        webResponse.setName("Web Application");
        webResponse.setDuration(12);

        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenReturn(webResponse);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(webRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(jsonPath("$.data.name", is("Web Application")))
                .andExpect(jsonPath("$.data.duration", is(12)));

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testCreateProject_ShortDuration() throws Exception {
        ProjectRequestDTO shortRequest = new ProjectRequestDTO();
        shortRequest.setName("Bug Fix");
        shortRequest.setDuration(1);

        ProjectResponseDTO shortResponse = new ProjectResponseDTO();
        shortResponse.setId(3L);
        shortResponse.setName("Bug Fix");
        shortResponse.setDuration(1);

        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenReturn(shortResponse);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(shortRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.duration", is(1)));

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testGetProjectById_Success() throws Exception {
        when(projectService.getProjectById(1L))
                .thenReturn(projectResponseDTO);

        mockMvc.perform(get("/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Mobile App Development")))
                .andExpect(jsonPath("$.data.duration", is(6)));

        verify(projectService, times(1)).getProjectById(1L);
    }

    @Test
    public void testGetProjectById_DifferentProject() throws Exception {
        ProjectResponseDTO dataProject = new ProjectResponseDTO();
        dataProject.setId(4L);
        dataProject.setName("Data Analysis Tool");
        dataProject.setDuration(8);

        when(projectService.getProjectById(4L))
                .thenReturn(dataProject);

        mockMvc.perform(get("/projects/4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(4)))
                .andExpect(jsonPath("$.data.name", is("Data Analysis Tool")))
                .andExpect(jsonPath("$.data.duration", is(8)));

        verify(projectService, times(1)).getProjectById(4L);
    }

    @Test
    public void testGetAllProjects_Success() throws Exception {
        ProjectResponseDTO proj1 = new ProjectResponseDTO();
        proj1.setId(1L);
        proj1.setName("Mobile App Development");
        proj1.setDuration(6);

        ProjectResponseDTO proj2 = new ProjectResponseDTO();
        proj2.setId(2L);
        proj2.setName("Web Application");
        proj2.setDuration(12);

        ProjectResponseDTO proj3 = new ProjectResponseDTO();
        proj3.setId(3L);
        proj3.setName("Data Analysis Tool");
        proj3.setDuration(8);

        List<ProjectResponseDTO> projects = Arrays.asList(proj1, proj2, proj3);
        Page<ProjectResponseDTO> page = new PageImpl<>(projects, PageRequest.of(0, 10), 3);

        when(projectService.getAllProjects(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(3)))
                .andExpect(jsonPath("$.data.content[0].id", is(1)))
                .andExpect(jsonPath("$.data.content[0].name", is("Mobile App Development")))
                .andExpect(jsonPath("$.data.content[1].id", is(2)))
                .andExpect(jsonPath("$.data.content[1].name", is("Web Application")))
                .andExpect(jsonPath("$.data.content[2].id", is(3)))
                .andExpect(jsonPath("$.data.content[2].name", is("Data Analysis Tool")));

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testGetAllProjects_Empty() throws Exception {
        Page<ProjectResponseDTO> emptyPage = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(projectService.getAllProjects(any(Pageable.class)))
                .thenReturn(emptyPage);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(0)));

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testGetAllProjects_SingleProject() throws Exception {
        List<ProjectResponseDTO> projects = Arrays.asList(projectResponseDTO);
        Page<ProjectResponseDTO> page = new PageImpl<>(projects, PageRequest.of(0, 10), 1);

        when(projectService.getAllProjects(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].name", is("Mobile App Development")));

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testUpdateProject_Success() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Mobile App Development - Phase 2");
        updateRequest.setDuration(8);

        ProjectResponseDTO updatedProj = new ProjectResponseDTO();
        updatedProj.setId(1L);
        updatedProj.setName("Mobile App Development - Phase 2");
        updatedProj.setDuration(8);

        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProj);

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(1)))
                .andExpect(jsonPath("$.data.name", is("Mobile App Development - Phase 2")))
                .andExpect(jsonPath("$.data.duration", is(8)));

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testUpdateProject_ChangeDurationOnly() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Mobile App Development");
        updateRequest.setDuration(10);

        ProjectResponseDTO updatedProj = new ProjectResponseDTO();
        updatedProj.setId(1L);
        updatedProj.setName("Mobile App Development");
        updatedProj.setDuration(10);

        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProj);

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.duration", is(10)));

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testUpdateProject_ChangeNameOnly() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Mobile Application");
        updateRequest.setDuration(6);

        ProjectResponseDTO updatedProj = new ProjectResponseDTO();
        updatedProj.setId(1L);
        updatedProj.setName("Mobile Application");
        updatedProj.setDuration(6);

        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProj);

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Mobile Application")));

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testUpdateProject_DifferentProject() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Enterprise System");
        updateRequest.setDuration(24);

        ProjectResponseDTO updatedProj = new ProjectResponseDTO();
        updatedProj.setId(5L);
        updatedProj.setName("Enterprise System");
        updatedProj.setDuration(24);

        when(projectService.updateProject(eq(5L), any(ProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProj);

        mockMvc.perform(put("/projects/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(5)))
                .andExpect(jsonPath("$.data.name", is("Enterprise System")))
                .andExpect(jsonPath("$.data.duration", is(24)));

        verify(projectService, times(1)).updateProject(eq(5L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteProject_Success() throws Exception {
        doNothing().when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Project deleted successfully")));

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    public void testDeleteProject_DifferentId() throws Exception {
        doNothing().when(projectService).deleteProject(2L);

        mockMvc.perform(delete("/projects/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Project deleted successfully")));

        verify(projectService, times(1)).deleteProject(2L);
    }

    @Test
    public void testCreateProject_LongName() throws Exception {
        ProjectRequestDTO longNameRequest = new ProjectRequestDTO();
        longNameRequest.setName("Very Long Project Name For Testing Purpose");
        longNameRequest.setDuration(5);

        ProjectResponseDTO longNameResponse = new ProjectResponseDTO();
        longNameResponse.setId(6L);
        longNameResponse.setName("Very Long Project Name For Testing Purpose");
        longNameResponse.setDuration(5);

        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenReturn(longNameResponse);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(longNameRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name", is("Very Long Project Name For Testing Purpose")));

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testGetAllProjects_MultipleProjects() throws Exception {
        ProjectResponseDTO proj1 = new ProjectResponseDTO();
        proj1.setId(1L);
        proj1.setName("Mobile App Development");
        proj1.setDuration(6);

        ProjectResponseDTO proj2 = new ProjectResponseDTO();
        proj2.setId(2L);
        proj2.setName("Web Application");
        proj2.setDuration(12);

        ProjectResponseDTO proj3 = new ProjectResponseDTO();
        proj3.setId(3L);
        proj3.setName("Data Analysis Tool");
        proj3.setDuration(8);

        ProjectResponseDTO proj4 = new ProjectResponseDTO();
        proj4.setId(4L);
        proj4.setName("Cloud Migration");
        proj4.setDuration(15);

        ProjectResponseDTO proj5 = new ProjectResponseDTO();
        proj5.setId(5L);
        proj5.setName("Security Enhancement");
        proj5.setDuration(10);

        List<ProjectResponseDTO> projects = Arrays.asList(proj1, proj2, proj3, proj4, proj5);
        Page<ProjectResponseDTO> page = new PageImpl<>(projects, PageRequest.of(0, 10), 5);

        when(projectService.getAllProjects(any(Pageable.class)))
                .thenReturn(page);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content", hasSize(5)))
                .andExpect(jsonPath("$.data.content[0].name", is("Mobile App Development")))
                .andExpect(jsonPath("$.data.content[1].name", is("Web Application")))
                .andExpect(jsonPath("$.data.content[2].name", is("Data Analysis Tool")))
                .andExpect(jsonPath("$.data.content[3].name", is("Cloud Migration")))
                .andExpect(jsonPath("$.data.content[4].name", is("Security Enhancement")));

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testUpdateProject_SameValues() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Mobile App Development");
        updateRequest.setDuration(6);

        ProjectResponseDTO updatedProj = new ProjectResponseDTO();
        updatedProj.setId(1L);
        updatedProj.setName("Mobile App Development");
        updatedProj.setDuration(6);

        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequestDTO.class)))
                .thenReturn(updatedProj);

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name", is("Mobile App Development")))
                .andExpect(jsonPath("$.data.duration", is(6)));

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testCreateProject_EmptyName() throws Exception {
        ProjectRequestDTO emptyRequest = new ProjectRequestDTO();
        emptyRequest.setName("");
        emptyRequest.setDuration(5);

        ProjectResponseDTO emptyResponse = new ProjectResponseDTO();
        emptyResponse.setId(7L);
        emptyResponse.setName("");
        emptyResponse.setDuration(5);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testGetProjectById_NotFound() throws Exception {
        when(projectService.getProjectById(999L))
                .thenThrow(new ProjectNotFoundException("Project not found with id: 999"));

        mockMvc.perform(get("/projects/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).getProjectById(999L);
    }

    @Test
    public void testCreateProject_ServiceException() throws Exception {
        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenThrow(new RuntimeException("Database connection error"));

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDTO)))
                .andExpect(status().isInternalServerError());

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testUpdateProject_NotFound() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Updated Project");
        updateRequest.setDuration(10);

        when(projectService.updateProject(eq(999L), any(ProjectUpdateRequestDTO.class)))
                .thenThrow(new ProjectNotFoundException("Project not found with id: 999"));

        mockMvc.perform(put("/projects/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).updateProject(eq(999L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteProject_NotFound() throws Exception {
        doThrow(new ProjectNotFoundException("Project not found with id: 999"))
                .when(projectService).deleteProject(999L);

        mockMvc.perform(delete("/projects/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(projectService, times(1)).deleteProject(999L);
    }

    @Test
    public void testGetAllProjects_ServiceException() throws Exception {
        when(projectService.getAllProjects(any(Pageable.class)))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(projectService, times(1)).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testCreateProject_NullResponse() throws Exception {
        when(projectService.createProject(any(ProjectRequestDTO.class)))
                .thenReturn(null);

        mockMvc.perform(post("/projects")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(projectRequestDTO)))
                .andExpect(status().isCreated());

        verify(projectService, times(1)).createProject(any(ProjectRequestDTO.class));
    }

    @Test
    public void testUpdateProject_WithException() throws Exception {
        ProjectUpdateRequestDTO updateRequest = new ProjectUpdateRequestDTO();
        updateRequest.setName("Cloud Project");
        updateRequest.setDuration(9);

        when(projectService.updateProject(eq(1L), any(ProjectUpdateRequestDTO.class)))
                .thenThrow(new RuntimeException("Update failed"));

        mockMvc.perform(put("/projects/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isInternalServerError());

        verify(projectService, times(1)).updateProject(eq(1L), any(ProjectUpdateRequestDTO.class));
    }

    @Test
    public void testDeleteProject_WithException() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(projectService).deleteProject(1L);

        mockMvc.perform(delete("/projects/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        verify(projectService, times(1)).deleteProject(1L);
    }

    @Test
    public void testGetProjectById_WithDifferentId() throws Exception {
        ProjectResponseDTO project2 = new ProjectResponseDTO();
        project2.setId(2L);
        project2.setName("Web Platform");
        project2.setDuration(14);

        when(projectService.getProjectById(2L))
                .thenReturn(project2);

        mockMvc.perform(get("/projects/2")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id", is(2)))
                .andExpect(jsonPath("$.data.name", is("Web Platform")))
                .andExpect(jsonPath("$.data.duration", is(14)));

        verify(projectService, times(1)).getProjectById(2L);
    }

    @Test
    public void testGetAllProjects_NullResponse() throws Exception {
        when(projectService.getAllProjects(any(Pageable.class))).thenReturn(null);

        mockMvc.perform(get("/projects")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
        
        verify(projectService).getAllProjects(any(Pageable.class));
    }

    @Test
    public void testGetAllProjectsList_Success() throws Exception {
        when(projectService.getAllProjects()).thenReturn(Arrays.asList(projectResponseDTO));

        mockMvc.perform(get("/projects/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
        
        verify(projectService).getAllProjects();
    }

    @Test
    public void testGetAllProjectsList_Exception() throws Exception {
        when(projectService.getAllProjects()).thenThrow(new RuntimeException("Fetch failed"));

        mockMvc.perform(get("/projects/all")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        
        verify(projectService).getAllProjects();
    }

    @Test
    public void testAssignEmployeeToProject_Success() throws Exception {
        doNothing().when(projectService).assignEmployeeToProject(1L, 1L);

        mockMvc.perform(post("/projects/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Employee assigned to project successfully")));
        
        verify(projectService).assignEmployeeToProject(1L, 1L);
    }

    @Test
    public void testAssignEmployeeToProject_Exception() throws Exception {
        doThrow(new RuntimeException("Assign failed")).when(projectService).assignEmployeeToProject(1L, 1L);

        mockMvc.perform(post("/projects/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        
        verify(projectService).assignEmployeeToProject(1L, 1L);
    }

    @Test
    public void testRemoveEmployeeFromProject_Success() throws Exception {
        doNothing().when(projectService).removeEmployeeFromProject(1L, 1L);

        mockMvc.perform(delete("/projects/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message", is("Employee removed from project successfully")));
        
        verify(projectService).removeEmployeeFromProject(1L, 1L);
    }

    @Test
    public void testRemoveEmployeeFromProject_Exception() throws Exception {
        doThrow(new RuntimeException("Remove failed")).when(projectService).removeEmployeeFromProject(1L, 1L);

        mockMvc.perform(delete("/projects/1/employees/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        
        verify(projectService).removeEmployeeFromProject(1L, 1L);
    }

    @Test
    public void testGetEmployeesByProject_Success() throws Exception {
        EmployeeResponseDTO emp = new EmployeeResponseDTO();
        emp.setId(1L);
        emp.setName("John Doe");
        
        when(projectService.getEmployeesByProjectId(1L)).thenReturn(Arrays.asList(emp));

        mockMvc.perform(get("/projects/1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].name", is("John Doe")));
        
        verify(projectService).getEmployeesByProjectId(1L);
    }

    @Test
    public void testGetEmployeesByProject_Exception() throws Exception {
        when(projectService.getEmployeesByProjectId(1L)).thenThrow(new RuntimeException("Fetch failed"));

        mockMvc.perform(get("/projects/1/employees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        
        verify(projectService).getEmployeesByProjectId(1L);
    }

    @Test
    public void testGetProjectsByEmployee_Success() throws Exception {
        when(projectService.getProjectsByEmployeeId(1L)).thenReturn(Arrays.asList(projectResponseDTO));

        mockMvc.perform(get("/projects/employee/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)));
        
        verify(projectService).getProjectsByEmployeeId(1L);
    }

    @Test
    public void testGetProjectsByEmployee_Exception() throws Exception {
        when(projectService.getProjectsByEmployeeId(1L)).thenThrow(new RuntimeException("Fetch failed"));

        mockMvc.perform(get("/projects/employee/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
        
        verify(projectService).getProjectsByEmployeeId(1L);
    }
}
