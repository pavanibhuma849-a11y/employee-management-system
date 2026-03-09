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
    Page<EmployeeResponseDTO> getEmployeesByDepartmentId(Long departmentId, Pageable pageable);
    EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO employeeDTO);
    void deleteEmployee(Long id);
    List<EmployeeResponseDTO> getAllEmployeesSortedByNameAndDate();
    List<EmployeeResponseDTO> getAllEmployeesSortedBySalary();
    
    java.util.TreeSet<com.company.ems.model.Employee> getEmployeesInTreeSet();
    java.util.Map<Long, com.company.ems.model.Employee> getEmployeeMapById();
}
