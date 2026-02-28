package com.company.ems.service;

import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.exception.InvalidProjectDurationException;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.model.Project;
import com.company.ems.repository.ProjectRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements IProjectService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectServiceImpl.class);

    @Autowired
    private ProjectRepository projectRepository;

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

    private ProjectResponseDTO mapToResponseDTO(Project project) {
        try {
            ProjectResponseDTO dto = new ProjectResponseDTO();
            dto.setId(project.getId());
            dto.setName(project.getName());
            dto.setDuration(project.getDuration());
            return dto;
        } catch (Exception ex) {
            logger.error("Error mapping Project to ProjectResponseDTO: {}", ex.getMessage(), ex);
            throw ex;
        }
    }
}
