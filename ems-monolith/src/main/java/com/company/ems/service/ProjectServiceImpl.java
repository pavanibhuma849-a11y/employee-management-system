package com.company.ems.service;

import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.exception.InvalidProjectDurationException;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.exception.ResourceConflictException;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements IProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public ProjectResponseDTO createProject(ProjectRequestDTO projectDTO) {
        try {
            logger.debug("Creating project with name: {}", projectDTO.getName());
            
            if (projectDTO.getDuration() != null && projectDTO.getDuration() <= 0) {
                throw new InvalidProjectDurationException("Project duration must be greater than zero");
            }
            
            Project project = new Project();
            project.setName(projectDTO.getName());
            project.setDuration(projectDTO.getDuration());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());
            Project saved = projectRepository.save(project);
            logger.info("Project created successfully with id: {}", saved.getId());
            return mapToResponseDTO(saved);
        } catch (InvalidProjectDurationException ex) {
            logger.warn("Invalid project duration: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error creating project: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public ProjectResponseDTO getProjectById(Long id) {
        try {
            logger.debug("Fetching project with id: {}", id);
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
            logger.info("Project fetched successfully with id: {}", id);
            return mapToResponseDTO(project);
        } catch (ProjectNotFoundException ex) {
            logger.warn("Project not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public Page<ProjectResponseDTO> getAllProjects(Pageable pageable) {
        try {
            logger.debug("Fetching paginated projects - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
            Page<Project> projects = projectRepository.findAll(pageable);
            logger.info("All projects fetched successfully - total elements: {}", projects.getTotalElements());
            return projects.map(this::mapToResponseDTO);
        } catch (Exception ex) {
            logger.error("Error fetching all projects: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<ProjectResponseDTO> getAllProjects() {
        try {
            logger.debug("Fetching all projects");
            List<ProjectResponseDTO> projects = projectRepository.findAll().stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            logger.info("All projects fetched successfully - total: {}", projects.size());
            return projects;
        } catch (Exception ex) {
            logger.error("Error fetching all projects: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public ProjectResponseDTO updateProject(Long id, ProjectUpdateRequestDTO projectDTO) {
        try {
            logger.debug("Updating project with id: {}", id);
            
            if (projectDTO.getDuration() != null && projectDTO.getDuration() <= 0) {
                throw new InvalidProjectDurationException("Project duration must be greater than zero");
            }
            
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
            project.setName(projectDTO.getName());
            project.setDuration(projectDTO.getDuration());
            project.setStartDate(projectDTO.getStartDate());
            project.setEndDate(projectDTO.getEndDate());
            Project updated = projectRepository.save(project);
            logger.info("Project updated successfully with id: {}", id);
            return mapToResponseDTO(updated);
        } catch (ProjectNotFoundException | InvalidProjectDurationException ex) {
            logger.warn("Error updating project: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteProject(Long id) {
        try {
            logger.debug("Deleting project with id: {}", id);
            Project project = projectRepository.findById(id)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + id));
            projectRepository.delete(project);
            logger.info("Project deleted successfully with id: {}", id);
        } catch (ProjectNotFoundException ex) {
            logger.warn("Project not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error deleting project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void assignEmployeeToProject(Long projectId, Long employeeId) {
        try {
            logger.debug("Assigning employee with id {} to project with id {}", employeeId, projectId);
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

            if (employee.getProjects().contains(project)) {
                throw new DuplicateResourceException("Employee already assigned to this project");
            }

            employee.getProjects().add(project);
            employeeRepository.save(employee);
            logger.info("Employee with id {} successfully assigned to project with id {}", employeeId, projectId);
        } catch (ProjectNotFoundException | EmployeeNotFoundException | DuplicateResourceException ex) {
            logger.warn("Error assigning employee to project: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error assigning employee with id {} to project with id {}: {}", employeeId, projectId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void removeEmployeeFromProject(Long projectId, Long employeeId) {
        try {
            logger.debug("Removing employee with id {} from project with id {}", employeeId, projectId);
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));

            if (!employee.getProjects().contains(project)) {
                throw new ResourceConflictException("Employee is not assigned to this project");
            }

            employee.getProjects().remove(project);
            employeeRepository.save(employee);
            logger.info("Employee with id {} successfully removed from project with id {}", employeeId, projectId);
        } catch (ProjectNotFoundException | EmployeeNotFoundException | ResourceConflictException ex) {
            logger.warn("Error removing employee from project: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error removing employee with id {} from project with id {}: {}", employeeId, projectId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByProjectId(Long projectId) {
        try {
            logger.debug("Fetching employees for project with id: {}", projectId);
            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId));
            
            List<EmployeeResponseDTO> employees = project.getEmployees().stream()
                    .map(this::mapEmployeeToResponseDTO)
                    .collect(Collectors.toList());
            
            logger.info("Employees fetched for project with id {} - total: {}", projectId, employees.size());
            return employees;
        } catch (ProjectNotFoundException ex) {
            logger.warn("Project not found with id: {}", projectId);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching employees for project with id {}: {}", projectId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<ProjectResponseDTO> getProjectsByEmployeeId(Long employeeId) {
        try {
            logger.debug("Fetching projects for employee with id: {}", employeeId);
            Employee employee = employeeRepository.findById(employeeId)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + employeeId));
            
            List<ProjectResponseDTO> projects = employee.getProjects().stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            
            logger.info("Projects fetched for employee with id {} - total: {}", employeeId, projects.size());
            return projects;
        } catch (EmployeeNotFoundException ex) {
            logger.warn("Employee not found with id: {}", employeeId);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching projects for employee with id {}: {}", employeeId, ex.getMessage(), ex);
            throw ex;
        }
    }

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        try {
            ProjectResponseDTO dto = new ProjectResponseDTO();
            dto.setId(project.getId());
            dto.setName(project.getName());
            dto.setDuration(project.getDuration());
            dto.setStartDate(project.getStartDate());
            dto.setEndDate(project.getEndDate());
            return dto;
        } catch (Exception ex) {
            logger.error("Error mapping Project to ProjectResponseDTO: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private EmployeeResponseDTO mapEmployeeToResponseDTO(Employee employee) {
        try {
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
        } catch (Exception ex) {
            logger.error("Error mapping Employee to EmployeeResponseDTO: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
