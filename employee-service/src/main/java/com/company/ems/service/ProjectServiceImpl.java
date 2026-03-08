package com.company.ems.service;

import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.exception.ResourceConflictException;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements IProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectDTO) {
        Project project = new Project();
        project.setName(projectDTO.getName());
        project.setDuration(projectDTO.getDuration());
        Project saved = projectRepository.save(project);
        return mapToResponseDTO(saved);
    }

    @Override
    public ProjectResponseDTO getProjectById(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
        return mapToResponseDTO(project);
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        return projectRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, ProjectUpdateRequestDTO projectDTO) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
        project.setName(projectDTO.getName());
        project.setDuration(projectDTO.getDuration());
        Project updated = projectRepository.save(project);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
        projectRepository.delete(project);
    }

    @Override
    public void assignEmployeeToProject(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (employee.getProjects().contains(project)) {
            throw new DuplicateResourceException("Employee already assigned to this project");
        }

        employee.getProjects().add(project);
        employeeRepository.save(employee);
    }

    @Override
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        if (!employee.getProjects().contains(project)) {
            throw new ResourceConflictException("Employee is not assigned to this project");
        }

        employee.getProjects().remove(project);
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByProjectId(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));

        return project.getEmployees().stream()
                .map(this::mapEmployeeToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProjectResponseDTO> getProjectsByEmployeeId(Long employeeId) {
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

        return employee.getProjects().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        ProjectResponseDTO dto = new ProjectResponseDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDuration(project.getDuration());
        return dto;
    }

    private EmployeeResponseDTO mapEmployeeToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setRole(employee.getRole());
        dto.setSalary(employee.getSalary());
        dto.setJoiningDate(employee.getJoiningDate());
        dto.setCreatedAt(employee.getCreatedAt());
        dto.setUpdatedAt(employee.getUpdatedAt());
        if (employee.getDepartment() != null) {
            dto.setDepartmentName(employee.getDepartment().getName());
        }
        if (employee.getProjects() != null) {
            dto.setProjectNames(employee.getProjects().stream()
                    .map(Project::getName)
                    .collect(Collectors.toSet()));
        }
        return dto;
    }
}
