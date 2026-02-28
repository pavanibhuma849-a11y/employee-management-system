package com.company.ems.service;

import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.model.Project;
import com.company.ems.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private ProjectRequestDTO projectRequestDTO;
    private ProjectResponseDTO projectResponseDTO;

    @BeforeEach
    public void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Mobile App Development");
        project.setDuration(6);

        projectRequestDTO = new ProjectRequestDTO();
        projectRequestDTO.setName("Mobile App Development");
        projectRequestDTO.setDuration(6);

        projectResponseDTO = new ProjectResponseDTO();
        projectResponseDTO.setId(1L);
        projectResponseDTO.setName("Mobile App Development");
        projectResponseDTO.setDuration(6);
    }

    @Test
    public void testCreateProject_Success() {
        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO result = projectService.createProject(projectRequestDTO);

        assertNotNull(result);
        assertEquals("Mobile App Development", result.getName());
        assertEquals(6, result.getDuration());
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testCreateProject_WithDifferentDuration() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Web Application");
        requestDTO.setDuration(12);

        Project webProject = new Project();
        webProject.setId(2L);
        webProject.setName("Web Application");
        webProject.setDuration(12);

        when(projectRepository.save(any(Project.class))).thenReturn(webProject);

        ProjectResponseDTO result = projectService.createProject(requestDTO);

        assertNotNull(result);
        assertEquals("Web Application", result.getName());
        assertEquals(12, result.getDuration());
        assertEquals(2L, result.getId());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testCreateProject_WithMinimalDuration() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Bug Fix");
        requestDTO.setDuration(1);

        Project bugFixProject = new Project();
        bugFixProject.setId(3L);
        bugFixProject.setName("Bug Fix");
        bugFixProject.setDuration(1);

        when(projectRepository.save(any(Project.class))).thenReturn(bugFixProject);

        ProjectResponseDTO result = projectService.createProject(requestDTO);

        assertNotNull(result);
        assertEquals("Bug Fix", result.getName());
        assertEquals(1, result.getDuration());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testCreateProject_WithLongDuration() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Enterprise System");
        requestDTO.setDuration(48);

        Project enterpriseProject = new Project();
        enterpriseProject.setId(4L);
        enterpriseProject.setName("Enterprise System");
        enterpriseProject.setDuration(48);

        when(projectRepository.save(any(Project.class))).thenReturn(enterpriseProject);

        ProjectResponseDTO result = projectService.createProject(requestDTO);

        assertNotNull(result);
        assertEquals("Enterprise System", result.getName());
        assertEquals(48, result.getDuration());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testGetProjectById_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        ProjectResponseDTO result = projectService.getProjectById(1L);

        assertNotNull(result);
        assertEquals("Mobile App Development", result.getName());
        assertEquals(6, result.getDuration());
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProjectById_ProjectNotFound() {
        when(projectRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(999L));
        verify(projectRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetProjectById_WithZeroId() {
        when(projectRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(0L));
        verify(projectRepository, times(1)).findById(0L);
    }

    @Test
    public void testGetProjectById_WithNegativeId() {
        when(projectRepository.findById(-1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(-1L));
        verify(projectRepository, times(1)).findById(-1L);
    }

    @Test
    public void testGetAllProjects_Success() {
        Project project1 = new Project();
        project1.setId(1L);
        project1.setName("Mobile App Development");
        project1.setDuration(6);

        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Web Application");
        project2.setDuration(12);

        Project project3 = new Project();
        project3.setId(3L);
        project3.setName("Data Analysis Tool");
        project3.setDuration(8);

        List<Project> projects = Arrays.asList(project1, project2, project3);

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Mobile App Development", result.get(0).getName());
        assertEquals(6, result.get(0).getDuration());
        assertEquals("Web Application", result.get(1).getName());
        assertEquals(12, result.get(1).getDuration());
        assertEquals("Data Analysis Tool", result.get(2).getName());
        assertEquals(8, result.get(2).getDuration());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllProjects_EmptyList() {
        when(projectRepository.findAll()).thenReturn(new java.util.ArrayList<>());

        List<ProjectResponseDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllProjects_SingleProject() {
        List<Project> projects = Arrays.asList(project);

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mobile App Development", result.get(0).getName());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateProject_Success() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development - Enhanced");
        updateDTO.setDuration(8);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile App Development - Enhanced");
        updatedProject.setDuration(8);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Mobile App Development - Enhanced", result.getName());
        assertEquals(8, result.getDuration());
        assertEquals(1L, result.getId());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_ChangeDurationOnly() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development");
        updateDTO.setDuration(10);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile App Development");
        updatedProject.setDuration(10);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Mobile App Development", result.getName());
        assertEquals(10, result.getDuration());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_ChangeNameOnly() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile Application");
        updateDTO.setDuration(6);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile Application");
        updatedProject.setDuration(6);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Mobile Application", result.getName());
        assertEquals(6, result.getDuration());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_ProjectNotFound() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development");

        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.updateProject(999L, updateDTO));
        verify(projectRepository, times(1)).findById(999L);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_SameValues() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development");
        updateDTO.setDuration(6);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile App Development");
        updatedProject.setDuration(6);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Mobile App Development", result.getName());
        assertEquals(6, result.getDuration());
        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testDeleteProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));

        projectService.deleteProject(1L);

        verify(projectRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    public void testDeleteProject_ProjectNotFound() {
        when(projectRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.deleteProject(999L));
        verify(projectRepository, times(1)).findById(999L);
        verify(projectRepository, never()).delete(any(Project.class));
    }

    @Test
    public void testDeleteProject_VerifyCorrectProjectDeleted() {
        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Web Application");
        project2.setDuration(12);

        when(projectRepository.findById(2L)).thenReturn(Optional.of(project2));

        projectService.deleteProject(2L);

        verify(projectRepository, times(1)).findById(2L);
        verify(projectRepository, times(1)).delete(project2);
    }

    @Test
    public void testCreateProject_RepositoryException() {
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> projectService.createProject(projectRequestDTO));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testGetAllProjects_RepositoryException() {
        when(projectRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> projectService.getAllProjects());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateProject_RepositoryException() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Updated Project");
        updateDTO.setDuration(10);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> projectService.updateProject(1L, updateDTO));
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testDeleteProject_RepositoryException() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        doThrow(new RuntimeException("Delete failed")).when(projectRepository).delete(any(Project.class));

        assertThrows(RuntimeException.class, () -> projectService.deleteProject(1L));
        verify(projectRepository, times(1)).delete(project);
    }

    @Test
    public void testGetAllProjects_MultipleProjectsWithVariousDurations() {
        Project proj1 = new Project();
        proj1.setId(1L);
        proj1.setName("Mobile App Development");
        proj1.setDuration(6);

        Project proj2 = new Project();
        proj2.setId(2L);
        proj2.setName("Web Application");
        proj2.setDuration(12);

        Project proj3 = new Project();
        proj3.setId(3L);
        proj3.setName("Data Analysis Tool");
        proj3.setDuration(8);

        Project proj4 = new Project();
        proj4.setId(4L);
        proj4.setName("Quick Patch");
        proj4.setDuration(1);

        Project proj5 = new Project();
        proj5.setId(5L);
        proj5.setName("Enterprise Overhaul");
        proj5.setDuration(24);

        List<Project> projects = Arrays.asList(proj1, proj2, proj3, proj4, proj5);

        when(projectRepository.findAll()).thenReturn(projects);

        List<ProjectResponseDTO> result = projectService.getAllProjects();

        assertNotNull(result);
        assertEquals(5, result.size());
        assertEquals("Mobile App Development", result.get(0).getName());
        assertEquals(6, result.get(0).getDuration());
        assertEquals("Enterprise Overhaul", result.get(4).getName());
        assertEquals(24, result.get(4).getDuration());
        verify(projectRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateProject_IncreaseDuration() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development");
        updateDTO.setDuration(18);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile App Development");
        updatedProject.setDuration(18);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals(18, result.getDuration());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testUpdateProject_DecreaseDuration() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Mobile App Development");
        updateDTO.setDuration(3);

        Project updatedProject = new Project();
        updatedProject.setId(1L);
        updatedProject.setName("Mobile App Development");
        updatedProject.setDuration(3);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(projectRepository.save(any(Project.class))).thenReturn(updatedProject);

        ProjectResponseDTO result = projectService.updateProject(1L, updateDTO);

        assertNotNull(result);
        assertEquals(3, result.getDuration());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    public void testCreateProject_WithMaximumDuration() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Long Term Project");
        requestDTO.setDuration(120);

        Project savedProject = new Project();
        savedProject.setId(6L);
        savedProject.setName("Long Term Project");
        savedProject.setDuration(120);

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponseDTO result = projectService.createProject(requestDTO);

        assertNotNull(result);
        assertEquals("Long Term Project", result.getName());
        assertEquals(120, result.getDuration());
        assertEquals(6L, result.getId());
    }

    @Test
    public void testGetProjectById_WithDifferentProjects() {
        Project project2 = new Project();
        project2.setId(2L);
        project2.setName("Web Application");
        project2.setDuration(12);

        when(projectRepository.findById(2L)).thenReturn(Optional.of(project2));

        ProjectResponseDTO result = projectService.getProjectById(2L);

        assertNotNull(result);
        assertEquals("Web Application", result.getName());
        assertEquals(12, result.getDuration());
        assertEquals(2L, result.getId());
        verify(projectRepository, times(1)).findById(2L);
    }

    @Test
    public void testCreateProject_WithSpecialCharactersInName() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("AI/ML Research & Development");
        requestDTO.setDuration(15);

        Project savedProject = new Project();
        savedProject.setId(7L);
        savedProject.setName("AI/ML Research & Development");
        savedProject.setDuration(15);

        when(projectRepository.save(any(Project.class))).thenReturn(savedProject);

        ProjectResponseDTO result = projectService.createProject(requestDTO);

        assertNotNull(result);
        assertEquals("AI/ML Research & Development", result.getName());
        assertEquals(15, result.getDuration());
    }

    @Test
    public void testGetProjectById_UnexpectedException() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class, () -> projectService.getProjectById(1L));
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProjectById_DataAccessException() {
        when(projectRepository.findById(1L)).thenThrow(new IllegalArgumentException("Invalid parameter"));

        assertThrows(IllegalArgumentException.class, () -> projectService.getProjectById(1L));
        verify(projectRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetProjectById_NullPointerException() {
        when(projectRepository.findById(1L)).thenThrow(new NullPointerException("Null value encountered"));

        assertThrows(NullPointerException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    public void testGetProjectById_IllegalStateException() {
        when(projectRepository.findById(1L)).thenThrow(new IllegalStateException("Invalid state"));

        assertThrows(IllegalStateException.class, () -> projectService.getProjectById(1L));
    }

    @Test
    public void testMapToResponseDTO_ExceptionHandling() {
        Project testProject = new Project();
        testProject.setId(1L);
        testProject.setName("Test Project");
        testProject.setDuration(5);

        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAll()).thenReturn(projects);

        assertDoesNotThrow(() -> projectService.getAllProjects());
    }

    @Test
    public void testUpdateProject_UnexpectedException() {
        ProjectUpdateRequestDTO updateDTO = new ProjectUpdateRequestDTO();
        updateDTO.setName("Updated Project");
        updateDTO.setDuration(10);

        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("Database unavailable"));

        assertThrows(RuntimeException.class, () -> projectService.updateProject(1L, updateDTO));
    }

    @Test
    public void testDeleteProject_UnexpectedException() {
        when(projectRepository.findById(1L)).thenThrow(new RuntimeException("Connection timeout"));

        assertThrows(RuntimeException.class, () -> projectService.deleteProject(1L));
    }
}
