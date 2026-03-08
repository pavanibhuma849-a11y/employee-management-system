package com.company.ems.controller;

import com.company.ems.dto.ApiResponse;
import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.service.IDepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/departments")
@Tag(name = "Department", description = "Operations related to Department Management")
public class DepartmentController {

    @Autowired
    private IDepartmentService departmentService;

    @PostMapping
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> createDepartment(@Valid @RequestBody DepartmentRequestDTO departmentDTO) {
        DepartmentResponseDTO response = departmentService.createDepartment(departmentDTO);
        ApiResponse<DepartmentResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), response, "Department created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> getDepartmentById(@PathVariable Long id) {
        DepartmentResponseDTO response = departmentService.getDepartmentById(id);
        ApiResponse<DepartmentResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Department fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<DepartmentResponseDTO>>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<DepartmentResponseDTO> response = departmentService.getAllDepartments(PageRequest.of(page, size));
        ApiResponse<Page<DepartmentResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Departments fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<DepartmentResponseDTO>>> getAllDepartmentsList() {
        List<DepartmentResponseDTO> response = departmentService.getAllDepartments();
        ApiResponse<List<DepartmentResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Departments fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<DepartmentResponseDTO>> updateDepartment(@PathVariable Long id, @Valid @RequestBody DepartmentUpdateRequestDTO departmentDTO) {
        DepartmentResponseDTO response = departmentService.updateDepartment(id, departmentDTO);
        ApiResponse<DepartmentResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Department updated successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteDepartment(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), null, "Department deleted successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/{id}/employees")
    public ResponseEntity<ApiResponse<List<EmployeeResponseDTO>>> getEmployeesByDepartment(@PathVariable Long id) {
        List<EmployeeResponseDTO> response = departmentService.getEmployeesByDepartment(id);
        ApiResponse<List<EmployeeResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Employees fetched successfully for department");
        return ResponseEntity.ok(apiResponse);
    }
}
