package com.company.ems.service;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import java.util.List;

public interface EmployeeService {
    EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeDTO);
    EmployeeResponseDTO getEmployeeById(Long id);
    List<EmployeeResponseDTO> getEmployeesByDepartment(String departmentName, int page, int size);
    List<EmployeeResponseDTO> getEmployeesSorted();
    EmployeeResponseDTO updateEmployee(Long id, EmployeeRequestDTO employeeDTO);
    void deleteEmployee(Long id);
}
