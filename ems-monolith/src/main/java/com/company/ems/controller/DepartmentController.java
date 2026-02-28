package com.company.ems.controller;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.service.IDepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department", description = "Operations related to Department Management")
public class DepartmentController {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentController.class);

    @Autowired
    private IDepartmentService departmentService;

    @PostMapping
    public ResponseEntity<DepartmentResponseDTO> createDepartment(@Valid @RequestBody DepartmentRequestDTO departmentDTO) {
        try {
            logger.info("Creating new department with name: {}", departmentDTO.getName());
            DepartmentResponseDTO response = departmentService.createDepartment(departmentDTO);
            if (response != null) {
                logger.info("Department created successfully with id: {}", response.getId());
            } else {
                logger.warn("Department creation returned null response");
            }
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error creating department: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> getDepartmentById(@PathVariable Long id) {
        try {
            logger.info("Fetching department with id: {}", id);
            DepartmentResponseDTO response = departmentService.getDepartmentById(id);
            logger.info("Department fetched successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<List<DepartmentResponseDTO>> getAllDepartments() {
        try {
            logger.info("Fetching all departments");
            List<DepartmentResponseDTO> response = departmentService.getAllDepartments();
            logger.info("Departments fetched successfully - total: {}", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching all departments: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentResponseDTO> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequestDTO departmentDTO) {
        try {
            logger.info("Updating department with id: {}", id);
            DepartmentResponseDTO response = departmentService.updateDepartment(id, departmentDTO);
            logger.info("Department updated successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error updating department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDepartment(@PathVariable Long id) {
        try {
            logger.info("Deleting department with id: {}", id);
            departmentService.deleteDepartment(id);
            logger.info("Department deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }
}
