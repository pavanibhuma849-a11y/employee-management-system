package com.company.ems.controller;

import com.company.ems.dto.ApiResponse;
import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.service.IEmployeeService;
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
@RequestMapping("/employees")
@Tag(name = "Employee", description = "Operations related to Employee Management")
public class EmployeeController {
    
    @Autowired
    private IEmployeeService employeeService;

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        EmployeeResponseDTO response = employeeService.createEmployee(employeeDTO);
        ApiResponse<EmployeeResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.CREATED.value(), response, "Employee created successfully");
        return ResponseEntity.status(HttpStatus.CREATED).body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        EmployeeResponseDTO response = employeeService.getEmployeeById(id);
        ApiResponse<EmployeeResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Employee fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<EmployeeResponseDTO>>> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EmployeeResponseDTO> response = employeeService.getEmployees(department, PageRequest.of(page, size));
        ApiResponse<Page<EmployeeResponseDTO>> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Employees fetched successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<EmployeeResponseDTO>> getSortedEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployeesSortedByNameAndDate());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeResponseDTO>> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequestDTO employeeDTO) {
        EmployeeResponseDTO response = employeeService.updateEmployee(id, employeeDTO);
        ApiResponse<EmployeeResponseDTO> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), response, "Employee updated successfully");
        return ResponseEntity.ok(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        ApiResponse<Void> apiResponse = new ApiResponse<>(HttpStatus.OK.value(), null, "employee remove from database succesfully");
        return ResponseEntity.ok(apiResponse);
    }
}



