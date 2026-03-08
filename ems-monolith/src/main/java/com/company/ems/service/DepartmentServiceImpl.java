package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceConflictException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    private static final Logger logger = LoggerFactory.getLogger(DepartmentServiceImpl.class);

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO) {
        try {
            logger.debug("Creating department with name: {}", departmentDTO.getName());
            
            if (departmentRepository.existsByName(departmentDTO.getName())) {
                throw new DuplicateResourceException("Department already exists with name: " + departmentDTO.getName());
            }
            
            Department department = new Department();
            department.setName(departmentDTO.getName());
            Department saved = departmentRepository.save(department);
            logger.info("Department created successfully with id: {}", saved.getId());
            return mapToResponseDTO(saved);
        } catch (DuplicateResourceException ex) {
            logger.warn("Duplicate department: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error creating department: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {
        try {
            logger.debug("Fetching department with id: {}", id);
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
            logger.info("Department fetched successfully with id: {}", id);
            return mapToResponseDTO(department);
        } catch (DepartmentNotFoundException ex) {
            logger.warn("Department not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public Page<DepartmentResponseDTO> getAllDepartments(Pageable pageable) {
        try {
            logger.debug("Fetching paginated departments - page: {}, size: {}", pageable.getPageNumber(), pageable.getPageSize());
            Page<Department> departments = departmentRepository.findAll(pageable);
            logger.info("All departments fetched successfully - total elements: {}", departments.getTotalElements());
            return departments.map(this::mapToResponseDTO);
        } catch (Exception ex) {
            logger.error("Error fetching all departments: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        try {
            logger.debug("Fetching all departments and sorting using TreeSet");
            List<Department> departments = departmentRepository.findAll();
            
            // Using TreeSet to demonstrate sorting with Comparable
            TreeSet<Department> sortedDepartments = new TreeSet<>(departments);
            
            List<DepartmentResponseDTO> response = sortedDepartments.stream()
                    .map(this::mapToResponseDTO)
                    .collect(Collectors.toList());
            
            logger.info("All departments fetched and sorted successfully - total: {}", response.size());
            return response;
        } catch (Exception ex) {
            logger.error("Error fetching all departments: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateRequestDTO departmentDTO) {
        try {
            logger.debug("Updating department with id: {}", id);
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
            department.setName(departmentDTO.getName());
            Department updated = departmentRepository.save(department);
            logger.info("Department updated successfully with id: {}", id);
            return mapToResponseDTO(updated);
        } catch (DepartmentNotFoundException ex) {
            logger.warn("Department not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteDepartment(Long id) {
        try {
            logger.debug("Deleting department with id: {}", id);
            Department department = departmentRepository.findById(id)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
            
            if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
                throw new ResourceConflictException("Cannot delete department with assigned employees: " + department.getName());
            }
            
            departmentRepository.delete(department);
            logger.info("Department deleted successfully with id: {}", id);
        } catch (DepartmentNotFoundException | ResourceConflictException ex) {
            logger.warn("Error deleting department: {}", ex.getMessage());
            throw ex;
        } catch (Exception ex) {
            logger.error("Error deleting department with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId) {
        try {
            logger.debug("Fetching employees for department with id: {}", departmentId);
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + departmentId));
            
            List<EmployeeResponseDTO> employees = department.getEmployees().stream()
                    .map(this::mapToEmployeeResponseDTO)
                    .collect(Collectors.toList());
            
            logger.info("Employees fetched successfully for department with id: {} - total: {}", departmentId, employees.size());
            return employees;
        } catch (DepartmentNotFoundException ex) {
            logger.warn("Department not found for employee fetching: {}", departmentId);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching employees for department {}: {}", departmentId, ex.getMessage(), ex);
            throw ex;
        }
    }

    private DepartmentResponseDTO mapToResponseDTO(Department department) {
        try {
            DepartmentResponseDTO dto = new DepartmentResponseDTO();
            dto.setId(department.getId());
            dto.setName(department.getName());
            return dto;
        } catch (Exception ex) {
            logger.error("Error mapping Department to DepartmentResponseDTO: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    private EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
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
