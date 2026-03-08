package com.company.ems.controller;

import com.company.ems.dto.ApiResponse;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import com.company.ems.service.IProjectService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/projects")
@Tag(name = "Project", description = "Operations related to Project Management")
public class ProjectController {

    @Autowired
    private IProjectService projectService;

    @PostMapping
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> createProject(@Valid @RequestBody ProjectRequestDTO projectDTO) {
        ProjectResponseDTO response = projectService.createProject(projectDTO);
        ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), response, "Project created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> getProjectById(@PathVariable Long id) {
        ProjectResponseDTO response = projectService.getProjectById(id);
        ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Project fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getAllProjects() {
        List<ProjectResponseDTO> response = projectService.getAllProjects();
        ApiResponse<List<ProjectResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Projects fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectResponseDTO>> updateProject(@PathVariable Long id, @Valid @RequestBody ProjectUpdateRequestDTO projectDTO) {
        ProjectResponseDTO response = projectService.updateProject(id, projectDTO);
        ApiResponse<ProjectResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Project updated successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), null, "Project deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PostMapping("/{id}/employees/{empId}")
    public ResponseEntity<ApiResponse<Void>> assignEmployeeToProject(@PathVariable Long id, @PathVariable Long empId) {
        projectService.assignEmployeeToProject(id, empId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), null, "Employee assigned to project successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}/employees/{empId}")
    public ResponseEntity<ApiResponse<Void>> removeEmployeeFromProject(@PathVariable Long id, @PathVariable Long empId) {
        projectService.removeEmployeeFromProject(id, empId);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.NO_CONTENT.value(), null, "Employee removed from project successfully");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getEmployeesByProject(@PathVariable Long id) {
        List<EmployeeResponseDTO> response = projectService.getEmployeesByProjectId(id);
        ApiResponse<List<EmployeeResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Employees fetched successfully for project");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/employee/{empId}")
    public ResponseEntity<ApiResponse<List<ProjectResponseDTO>>> getProjectsByEmployee(@PathVariable Long empId) {
        List<ProjectResponseDTO> response = projectService.getProjectsByEmployeeId(empId);
        ApiResponse<List<ProjectResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Projects fetched successfully for employee");
        return ResponseEntity.ok(apiResponse);
    }
}
