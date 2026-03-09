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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);
    
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public EmployeeResponseDTO createEmployee(EmployeeRequestDTO employeeDTO) {
        try {
            logger.debug("Creating employee with name: {}", employeeDTO.getName());
            Employee employee = mapToEntity(employeeDTO);
            Employee savedEmployee = employeeRepository.save(employee);
            logger.info("Employee created successfully with id: {}", savedEmployee.getId());
            return mapToResponseDTO(savedEmployee);
        } catch (Exception ex) {
            logger.error("Error creating employee: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public EmployeeResponseDTO getEmployeeById(Long id) {
        try {
            logger.debug("Fetching employee with id: {}", id);
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
            logger.info("Employee fetched successfully with id: {}", id);
            return mapToResponseDTO(employee);
        } catch (EmployeeNotFoundException ex) {
            logger.warn("Employee not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error fetching employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public Page<EmployeeResponseDTO> getEmployees(String departmentName, Pageable pageable) {
        try {
            logger.debug("Fetching employees - department: {}, page: {}, size: {}", departmentName, pageable.getPageNumber(), pageable.getPageSize());
            Page<Employee> employees;
            if (departmentName != null && !departmentName.isEmpty()) {
                employees = employeeRepository.findByDepartmentName(departmentName, pageable);
                logger.info("Employees fetched by department: {} - total: {}", departmentName, employees.getTotalElements());
            } else {
                employees = employeeRepository.findAll(pageable);
                logger.info("All employees fetched - total: {}", employees.getTotalElements());
            }
            return employees.map(this::mapToResponseDTO);
        } catch (Exception ex) {
            logger.error("Error fetching employees: {}", ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public Page<EmployeeResponseDTO> getEmployeesByDepartmentId(Long departmentId, Pageable pageable) {
        try {
            logger.debug("Fetching employees by departmentId: {}, page: {}, size: {}", departmentId, pageable.getPageNumber(), pageable.getPageSize());
            Page<Employee> employees = employeeRepository.findByDepartmentId(departmentId, pageable);
            logger.info("Employees fetched by departmentId: {} - total: {}", departmentId, employees.getTotalElements());
            return employees.map(this::mapToResponseDTO);
        } catch (Exception ex) {
            logger.error("Error fetching employees by departmentId {}: {}", departmentId, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public EmployeeResponseDTO updateEmployee(Long id, EmployeeUpdateRequestDTO employeeDTO) {
        try {
            logger.debug("Updating employee with id: {}", id);
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
            
            employee.setName(employeeDTO.getName());
            employee.setRole(employeeDTO.getRole());
            employee.setSalary(employeeDTO.getSalary());
            employee.setJoiningDate(employeeDTO.getJoiningDate());
            
            if (employeeDTO.getDepartmentId() != null) {
                try {
                    Department department = departmentRepository.findById(employeeDTO.getDepartmentId())
                            .orElseThrow(() -> new DepartmentNotFoundException("Department not found with id: " + employeeDTO.getDepartmentId()));
                    employee.setDepartment(department);
                    logger.debug("Department assigned to employee");
                } catch (Exception ex) {
                    logger.error("Error assigning department to employee: {}", ex.getMessage(), ex);
                    throw ex;
                }
            }

            if (employeeDTO.getProjectIds() != null) {
                java.util.Set<Project> projects = employeeDTO.getProjectIds().stream()
                        .map(projectId -> projectRepository.findById(projectId)
                                .orElseThrow(() -> new ProjectNotFoundException("Project not found with id: " + projectId)))
                        .collect(Collectors.toSet());
                employee.setProjects(projects);
                logger.debug("Projects assigned to employee");
            }

            Employee updatedEmployee = employeeRepository.save(employee);
            logger.info("Employee updated successfully with id: {}", id);
            return mapToResponseDTO(updatedEmployee);
        } catch (EmployeeNotFoundException ex) {
            logger.warn("Employee not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error updating employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public void deleteEmployee(Long id) {
        try {
            logger.debug("Deleting employee with id: {}", id);
            Employee employee = employeeRepository.findById(id)
                    .orElseThrow(() -> new EmployeeNotFoundException("Employee not found with id: " + id));
            employeeRepository.delete(employee);
            logger.info("Employee deleted successfully with id: {}", id);
        } catch (EmployeeNotFoundException ex) {
            logger.warn("Employee not found with id: {}", id);
            throw ex;
        } catch (Exception ex) {
            logger.error("Error deleting employee with id {}: {}", id, ex.getMessage(), ex);
            throw ex;
        }
    }

    @Override
    public List<EmployeeResponseDTO> getAllEmployeesSortedByNameAndDate() {
        List<Employee> employees = employeeRepository.findAll();
        
        // Custom sorting using Comparator (Requirement 3.1.55)
        employees.sort(Comparator.comparing(Employee::getName)
                .thenComparing(Employee::getJoiningDate));
                
        return employees.stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    @Override
    public TreeSet<Employee> getEmployeesInTreeSet() {
        // Requirement 3.1.52 & 3.1.53: TreeSet + Natural ordering (Comparable in Employee)
        return new TreeSet<>(employeeRepository.findAll());
    }

    @Override
    public Map<Long, Employee> getEmployeeMapById() {
        List<Employee> employees = employeeRepository.findAll();
        Map<Long, Employee> employeeMap = new HashMap<>(); // Requirement 3.1.52: HashMap
        for (Employee emp : employees) {
            employeeMap.put(emp.getId(), emp);
        }
        return employeeMap;
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
