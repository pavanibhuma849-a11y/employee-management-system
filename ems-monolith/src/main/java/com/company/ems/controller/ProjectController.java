package com.company.ems.controller;

import com.company.ems.dto.ApiResponse;
import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.service.IProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/projects")
@Tag(name = "Project", description = "Operations related to Project Management")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private IProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        try {
            logger.info("Creating new project with name: {}", projectDTO.getName());
            ProjectResponseDTO response = projectService.createProject(projectDTO);
            if (response != null) {
                logger.info("Project created successfully with id: {}", response.getId());
            } else {
                logger.warn("Project creation returned null response");
            }
            ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), response, "Project created successfully");
            return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
        } catch (Exception ex) {
            logger.error("Error creating project: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        try {
            logger.info("Fetching project with id: {}", id);
            ProjectResponseDTO response = projectService.getProjectById(id);
            logger.info("Project fetched successfully with id: {}", id);
            ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Project fetched successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            logger.error("Error fetching project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects() {
        try {
            logger.info("Fetching all projects");
            List<ProjectResponseDTO> response = projectService.getAllProjects();
            logger.info("Projects fetched successfully - total: {}", response.size());
            ApiResponse<List<ProjectResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Projects fetched successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            logger.error("Error fetching all projects: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequestDTO projectDTO) {
        try {
            logger.info("Updating project with id: {}", id);
            ProjectResponseDTO response = projectService.updateProject(id, projectDTO);
            logger.info("Project updated successfully with id: {}", id);
            ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Project updated successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            logger.error("Error updating project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        try {
            logger.info("Deleting project with id: {}", id);
            projectService.deleteProject(id);
            logger.info("Project deleted successfully with id: {}", id);
            ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), null, "Project deleted successfully");
            return ResponseEntity.ok(apiResponse);
        } catch (Exception ex) {
            logger.error("Error deleting project with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }
}

