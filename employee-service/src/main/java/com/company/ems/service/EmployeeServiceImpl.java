package com.company.ems.service;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.exception.ProjectNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements IEmployeeService {
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeDTO) {
        Employee employee = mapToEntity(employeeDTO);
        Employee savedEmployee = employeeRepository.save(employee);
        return mapToResponseDTO(savedEmployee);
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        return mapToResponseDTO(employee);
    }

    @Override
    public Page<EmployeeResponseDTO> getEmployees(String departmentName, Pageable pageable) {
        Page<Employee> employees;
        if (departmentName != null && !departmentName.isEmpty()) {
            employees = employeeRepository.findByDepartmentName(departmentName, pageable);
        } else {
            employees = employeeRepository.findAll(pageable);
        }
        return employees.map(this::mapToResponseDTO);
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO employeeDTO) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        
        employee.setName(employeeDTO.getName());
        employee.setRole(employeeDTO.getRole());
        employee.setSalary(employeeDTO.getSalary());
        employee.setJoiningDate(employeeDTO.getJoiningDate());
        
        if (employeeDTO.getDepartmentId() != null) {
            Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeDTO.getDepartmentId()));
            employee.setDepartment(department);
        }

        if (employeeDTO.getProjectIds() != null) {
            java.util.Set<Project> projects = employeeDTO.getProjectIds().stream()
                    .map(projectId -> projectRepository.findById(projectId)
                            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId)))
                    .collect(Collectors.toSet());
            employee.setProjects(projects);
        }

        Employee updatedEmployee = employeeRepository.save(employee);
        return mapToResponseDTO(updatedEmployee);
    }

    @Override
    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
        employeeRepository.delete(employee);
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployeesSortedByNameAndDate() {
        List<Employee> employees = employeeRepository.findAll();
        
        // Custom sorting using Comparator (Requirement 3.1.55)
        employees.sort(Comparator.comparing(Employee::getName)
                .thenComparing(Employee::getJoiningDate));
                
        return employees.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public Map<String, Double> getSalaryDistribution() {
        List<Employee> employees = employeeRepository.findAll();
        Map<String, Double> distribution = new HashMap<>(); // Requirement 3.1.52: HashMap
        for (Employee emp : employees) {
            String dept = emp.getDepartment() != null ? emp.getDepartment().getName() : "Unassigned";
            distribution.put(dept, distribution.getOrDefault(dept, 0.0) + emp.getSalary());
        }
        return distribution;
    }

    public TreeSet<Employee> getEmployeesSortedBySalary() {
        // Requirement 3.1.52 & 3.1.53: TreeSet + Natural ordering (Comparable in Employee)
        return new TreeSet<>(employeeRepository.findAll());
    }

    private Employee mapToEntity(EmployeeRequestDTO dto) {
        Employee employee = new Employee();
        employee.setName(dto.getName());
        employee.setRole(dto.getRole());
        employee.setSalary(dto.getSalary());
        employee.setJoiningDate(dto.getJoiningDate());
        
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + dto.getDepartmentId()));
            employee.setDepartment(department);
        }

        if (dto.getProjectIds() != null) {
            java.util.Set<Project> projects = dto.getProjectIds().stream()
                    .map(projectId -> projectRepository.findById(projectId)
                            .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId)))
                    .collect(Collectors.toSet());
            employee.setProjects(projects);
        }
        return employee;
    }

    private EmployeeResponseDTO mapToResponseDTO(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setName(employee.getName());
        dto.setRole(employee.getRole());
        dto.setSalary(employee.getSalary());
        dto.setJoiningDate(employee.getJoiningDate());
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
