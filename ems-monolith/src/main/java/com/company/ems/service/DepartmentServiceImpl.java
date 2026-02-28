package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.repository.DepartmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
            Department department = new Department();
            department.setName(departmentDTO.getName());
            Department saved = departmentRepository.save(department);
            logger.info("Department created successfully with id: {}", saved.getId());
            return mapToResponseDTO(saved);
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
            departmentRepository.delete(department);
            logger.info("Department deleted successfully with id: {}", id);
        } catch (DepartmentNotFoundException ex) {
            logger.warn("Department not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error deleting department with id {}: {}", id, ex.getMessage(), ex);
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
}
