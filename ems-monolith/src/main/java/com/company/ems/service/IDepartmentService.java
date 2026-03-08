package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface IDepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO);
    DepartmentResponseDTO getDepartmentById(Long id);
    Page<DepartmentResponseDTO> getAllDepartments(Pageable pageable);
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateRequestDTO departmentDTO);
    void deleteDepartment(Long id);
    List<EmployeeResponseDTO> getEmployeesByDepartment(Long departmentId);
}
