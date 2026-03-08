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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepartmentServiceImpl implements IDepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Override
    public DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO) {
        if (departmentRepository.existsByName(departmentDTO.getName())) {
            throw new DuplicateResourceException("Department already exists with name: " + departmentDTO.getName());
        }
        Department department = new Department();
        department.setName(departmentDTO.getName());
        Department saved = departmentRepository.save(department);
        return mapToResponseDTO(saved);
    }

    @Override
    public DepartmentResponseDTO getDepartmentById(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        return mapToResponseDTO(department);
    }

    @Override
    public Page<DepartmentResponseDTO> getAllDepartments(Pageable pageable) {
        return departmentRepository.findAll(pageable).map(this::mapToResponseDTO);
    }

    @Override
    public List<DepartmentResponseDTO> getAllDepartments() {
        return departmentRepository.findAll().stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateRequestDTO departmentDTO) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        department.setName(departmentDTO.getName());
        Department updated = departmentRepository.save(department);
        return mapToResponseDTO(updated);
    }

    @Override
    public void deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + id));
        
        if (department.getEmployees() != null && !department.getEmployees().isEmpty()) {
            throw new ResourceConflictException("Cannot delete department with assigned employees: " + department.getName());
        }
        
        departmentRepository.delete(department);
    }

    @Override
    public List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + departmentId));
        
        return department.getEmployees().stream()
                .map(this::mapToEmployeeResponseDTO)
                .collect(Collectors.toList());
    }

    private DepartmentResponseDTO mapToResponseDTO(Department department) {
        DepartmentResponseDTO dto = new DepartmentResponseDTO();
        dto.setId(department.getId());
        dto.setName(department.getName());
        return dto;
    }

    private EmployeeResponseDTO mapToEmployeeResponseDTO(Employee employee) {
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
