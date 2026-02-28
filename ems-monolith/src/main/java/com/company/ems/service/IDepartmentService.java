package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import java.util.List;

public interface IDepartmentService {
    DepartmentResponseDTO createDepartment(DepartmentRequestDTO departmentDTO);
    DepartmentResponseDTO getDepartmentById(Long id);
    List<DepartmentResponseDTO> getAllDepartments();
    DepartmentResponseDTO updateDepartment(Long id, DepartmentUpdateRequestDTO departmentDTO);
    void deleteDepartment(Long id);
}
