package com.company.ems.service;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IEmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeDTO);
    EmployeeResponseDTO getEmployeeById(Long id);
    Page<EmployeeResponseDTO> getEmployees(String departmentName, Pageable pageable);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO employeeDTO);
    void deleteEmployee(Long id);
    List<EmployeeResponseDTO> getAllEmployeesSortedByNameAndDate();
}
