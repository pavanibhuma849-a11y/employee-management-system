package com.company.ems.controller;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.service.IEmployeeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/employees")
@Tag(name = "Employee", description = "Operations related to Employee Management")
public class EmployeeController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private IEmployeeService employeeService;

    @PostMapping
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeDTO) {
        try {
            logger.info("Creating new employee with name: {}", employeeDTO.getName());
            EmployeeResponseDTO response = employeeService.createEmployee(employeeDTO);
            if (response != null) {
                logger.info("Employee created successfully with id: {}", response.getId());
            } else {
                logger.warn("Employee creation returned null response");
            }
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error creating employee: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployeeById(@PathVariable Long id) {
        try {
            logger.info("Fetching employee with id: {}", id);
            EmployeeResponseDTO response = employeeService.getEmployeeById(id);
            logger.info("Employee fetched successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDTO>> getEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            logger.info("Fetching employees - department: {}, page: {}, size: {}", department, page, size);
            Page<EmployeeResponseDTO> response = employeeService.getEmployees(department, PageRequest.of(page, size));
            logger.info("Employees fetched successfully - total elements: {}", response.getTotalElements());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching employees: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/sorted")
    public ResponseEntity<List<EmployeeResponseDTO>> getSortedEmployees() {
        try {
            logger.info("Fetching all employees sorted by name and date");
            List<EmployeeResponseDTO> response = employeeService.getAllEmployeesSortedByNameAndDate();
            logger.info("Sorted employees fetched successfully - total: {}", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching sorted employees: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @GetMapping("/sorted-by-salary")
    public ResponseEntity<List<EmployeeResponseDTO>> getEmployeesSortedBySalary() {
        try {
            logger.info("Fetching all employees sorted by salary");
            List<EmployeeResponseDTO> response = employeeService.getAllEmployeesSortedBySalary();
            logger.info("Employees sorted by salary fetched successfully - total: {}", response.size());
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error fetching employees sorted by salary: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeUpdateRequestDTO employeeDTO) {
        try {
            logger.info("Updating employee with id: {}", id);
            EmployeeResponseDTO response = employeeService.updateEmployee(id, employeeDTO);
            logger.info("Employee updated successfully with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (Exception ex) {
            logger.error("Error updating employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable Long id) {
        try {
            logger.info("Deleting employee with id: {}", id);
            employeeService.deleteEmployee(id);
            logger.info("Employee deleted successfully with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (Exception ex) {
            logger.error("Error deleting employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }
}
