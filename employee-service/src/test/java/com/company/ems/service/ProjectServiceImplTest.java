package com.company.ems.service;

import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    private Project project;
    private Employee employee;

    @BeforeEach
    void setUp() {
        project = new Project();
        project.setId(1L);
        project.setName("Apollo");
        project.setDuration(12);

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setProjects(new HashSet<>());
    }

    @Test
    void testCreateProject() {
        ProjectRequestDTO requestDTO = new ProjectRequestDTO();
        requestDTO.setName("Apollo");
        requestDTO.setDuration(12);

        when(projectRepository.save(any(Project.class))).thenReturn(project);

        ProjectResponseDTO response = projectService.createProject(requestDTO);

        assertNotNull(response);
        assertEquals("Apollo", response.getName());
        verify(projectRepository, times(1)).save(any(Project.class));
    }

    @Test
    void testAssignEmployeeToProject_Success() {
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        projectService.assignEmployeeToProject(1L, 1L);

        assertTrue(employee.getProjects().contains(project));
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    void testAssignEmployeeToProject_AlreadyAssigned() {
        employee.getProjects().add(project);
        when(projectRepository.findById(1L)).thenReturn(Optional.of(project));
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        assertThrows(DuplicateResourceException.class, () -> projectService.assignEmployeeToProject(1L, 1L));
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    void testGetProjectById_NotFound() {
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProjectNotFoundException.class, () -> projectService.getProjectById(1L));
    }
}
