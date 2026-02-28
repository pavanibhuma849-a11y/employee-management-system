package com.company.ems.service;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private EmployeeRequestDTO employeeRequestDTO;
    private EmployeeResponseDTO employeeResponseDTO;
    private Department department;

    @BeforeEach
    public void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setRole("Software Engineer");
        employee.setSalary(50000.0);
        employee.setJoiningDate(LocalDate.of(2022, 1, 15));
        employee.setDepartment(department);

        employeeRequestDTO = new EmployeeRequestDTO();
        employeeRequestDTO.setName("John Doe");
        employeeRequestDTO.setRole("Software Engineer");
        employeeRequestDTO.setSalary(50000.0);
        employeeRequestDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        employeeRequestDTO.setDepartmentId(1L);

        employeeResponseDTO = new EmployeeResponseDTO();
        employeeResponseDTO.setId(1L);
        employeeResponseDTO.setName("John Doe");
        employeeResponseDTO.setRole("Software Engineer");
        employeeResponseDTO.setSalary(50000.0);
        employeeResponseDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        employeeResponseDTO.setDepartmentName("IT");
    }

    @Test
    public void testCreateEmployee_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO result = employeeService.createEmployee(employeeRequestDTO);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals("Software Engineer", result.getRole());
        assertEquals(50000.0, result.getSalary());
        assertEquals("IT", result.getDepartmentName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateEmployee_DepartmentNotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(employeeRequestDTO));
        verify(departmentRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployee_WithoutDepartment() {
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("Jane Smith");
        requestDTO.setRole("Analyst");
        requestDTO.setSalary(40000.0);
        requestDTO.setJoiningDate(LocalDate.of(2023, 3, 20));
        requestDTO.setDepartmentId(null);

        Employee savedEmployee = new Employee();
        savedEmployee.setId(2L);
        savedEmployee.setName("Jane Smith");
        savedEmployee.setRole("Analyst");
        savedEmployee.setSalary(40000.0);
        savedEmployee.setJoiningDate(LocalDate.of(2023, 3, 20));

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("Jane Smith", result.getName());
        assertNull(result.getDepartmentName());
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(departmentRepository, never()).findById(anyLong());
    }

    @Test
    public void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals("John Doe", result.getName());
        assertEquals(1L, result.getId());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEmployeeById_EmployeeNotFound() {
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(999L));
        verify(employeeRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetEmployees_WithoutDepartmentFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee);
        Page<Employee> pageOfEmployees = new PageImpl<>(employees, pageable, 1);

        when(employeeRepository.findAll(pageable)).thenReturn(pageOfEmployees);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(employeeRepository, times(1)).findAll(pageable);
        verify(employeeRepository, never()).findByDepartmentName(anyString(), any(Pageable.class));
    }

    @Test
    public void testGetEmployees_WithDepartmentFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        List<Employee> employees = Arrays.asList(employee);
        Page<Employee> pageOfEmployees = new PageImpl<>(employees, pageable, 1);

        when(employeeRepository.findByDepartmentName("IT", pageable)).thenReturn(pageOfEmployees);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees("IT", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());
        verify(employeeRepository, times(1)).findByDepartmentName("IT", pageable);
        verify(employeeRepository, never()).findAll(pageable);
    }

    @Test
    public void testGetEmployees_EmptyDepartmentFilter() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> pageOfEmployees = new PageImpl<>(new java.util.ArrayList<>(), pageable, 0);

        when(employeeRepository.findAll(pageable)).thenReturn(pageOfEmployees);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees("", pageable);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testUpdateEmployee_Success() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("John Updated");
        updateDTO.setRole("Senior Engineer");
        updateDTO.setSalary(60000.0);
        updateDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        updateDTO.setDepartmentId(1L);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Updated");
        updatedEmployee.setRole("Senior Engineer");
        updatedEmployee.setSalary(60000.0);
        updatedEmployee.setJoiningDate(LocalDate.of(2022, 1, 15));
        updatedEmployee.setDepartment(department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, updateDTO);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        assertEquals("Senior Engineer", result.getRole());
        assertEquals(60000.0, result.getSalary());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_EmployeeNotFound() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("John Updated");
        
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.updateEmployee(999L, updateDTO));
        verify(employeeRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_DepartmentNotFound() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setDepartmentId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, updateDTO));
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    @Test
    public void testUpdateEmployee_WithoutDepartmentChange() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("John Updated");
        updateDTO.setRole("Senior Engineer");
        updateDTO.setSalary(60000.0);
        updateDTO.setJoiningDate(LocalDate.of(2022, 1, 15));
        updateDTO.setDepartmentId(null);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("John Updated");
        updatedEmployee.setRole("Senior Engineer");
        updatedEmployee.setSalary(60000.0);
        updatedEmployee.setJoiningDate(LocalDate.of(2022, 1, 15));
        updatedEmployee.setDepartment(department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, updateDTO);

        assertNotNull(result);
        assertEquals("John Updated", result.getName());
        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).save(any(Employee.class));
        verify(departmentRepository, never()).findById(anyLong());
    }

    @Test
    public void testDeleteEmployee_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).findById(1L);
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    public void testDeleteEmployee_EmployeeNotFound() {
        when(employeeRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(999L));
        verify(employeeRepository, times(1)).findById(999L);
        verify(employeeRepository, never()).delete(any(Employee.class));
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate() {
        Employee employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Alice Johnson");
        employee1.setRole("Developer");
        employee1.setSalary(55000.0);
        employee1.setJoiningDate(LocalDate.of(2021, 5, 10));

        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Bob Smith");
        employee2.setRole("Analyst");
        employee2.setSalary(45000.0);
        employee2.setJoiningDate(LocalDate.of(2022, 3, 15));

        Employee employee3 = new Employee();
        employee3.setId(3L);
        employee3.setName("Alice Johnson");
        employee3.setRole("Manager");
        employee3.setSalary(65000.0);
        employee3.setJoiningDate(LocalDate.of(2020, 1, 20));

        List<Employee> employees = Arrays.asList(employee2, employee1, employee3);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getAllEmployeesSortedByNameAndDate();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());
        assertEquals(LocalDate.of(2020, 1, 20), result.get(0).getJoiningDate());
        assertEquals("Alice Johnson", result.get(1).getName());
        assertEquals(LocalDate.of(2021, 5, 10), result.get(1).getJoiningDate());
        assertEquals("Bob Smith", result.get(2).getName());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate_EmptyList() {
        when(employeeRepository.findAll()).thenReturn(new java.util.ArrayList<>());

        List<EmployeeResponseDTO> result = employeeService.getAllEmployeesSortedByNameAndDate();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testCreateEmployee_RepositoryException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(employeeRequestDTO));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testGetEmployeeById_Multiple() {
        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Doe");
        employee2.setRole("Manager");
        employee2.setSalary(60000.0);

        when(employeeRepository.findById(2L)).thenReturn(Optional.of(employee2));

        EmployeeResponseDTO result = employeeService.getEmployeeById(2L);

        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals(2L, result.getId());
        verify(employeeRepository, times(1)).findById(2L);
    }

    @Test
    public void testUpdateEmployee_WithDifferentValues() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("Alice Wonder");
        updateDTO.setRole("Tech Lead");
        updateDTO.setSalary(75000.0);
        updateDTO.setJoiningDate(LocalDate.of(2021, 6, 20));
        updateDTO.setDepartmentId(1L);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(1L);
        updatedEmployee.setName("Alice Wonder");
        updatedEmployee.setRole("Tech Lead");
        updatedEmployee.setSalary(75000.0);
        updatedEmployee.setJoiningDate(LocalDate.of(2021, 6, 20));
        updatedEmployee.setDepartment(department);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Alice Wonder", result.getName());
        assertEquals("Tech Lead", result.getRole());
        assertEquals(75000.0, result.getSalary());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testGetEmployees_WithDepartmentFilterEmptyResult() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> emptyPage = new PageImpl<>(new java.util.ArrayList<>(), pageable, 0);

        when(employeeRepository.findByDepartmentName("NonExistent", pageable)).thenReturn(emptyPage);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees("NonExistent", pageable);

        assertNotNull(result);
        assertEquals(0, result.getContent().size());
        assertEquals(0, result.getTotalElements());
    }

    @Test
    public void testDeleteEmployee_RepositoryException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doThrow(new RuntimeException("Delete failed")).when(employeeRepository).delete(any(Employee.class));

        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate_MultipleWithSameName() {
        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setName("Alice Johnson");
        emp1.setRole("Developer");
        emp1.setSalary(55000.0);
        emp1.setJoiningDate(LocalDate.of(2021, 5, 10));

        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setName("Bob Smith");
        emp2.setRole("Analyst");
        emp2.setSalary(45000.0);
        emp2.setJoiningDate(LocalDate.of(2022, 3, 15));

        Employee emp3 = new Employee();
        emp3.setId(3L);
        emp3.setName("Alice Johnson");
        emp3.setRole("Manager");
        emp3.setSalary(65000.0);
        emp3.setJoiningDate(LocalDate.of(2020, 1, 20));

        Employee emp4 = new Employee();
        emp4.setId(4L);
        emp4.setName("Charlie Brown");
        emp4.setRole("Engineer");
        emp4.setSalary(58000.0);
        emp4.setJoiningDate(LocalDate.of(2019, 7, 5));

        List<Employee> employees = Arrays.asList(emp2, emp1, emp3, emp4);

        when(employeeRepository.findAll()).thenReturn(employees);

        List<EmployeeResponseDTO> result = employeeService.getAllEmployeesSortedByNameAndDate();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Alice Johnson", result.get(0).getName());
        assertEquals(LocalDate.of(2020, 1, 20), result.get(0).getJoiningDate());
        assertEquals("Alice Johnson", result.get(1).getName());
        assertEquals(LocalDate.of(2021, 5, 10), result.get(1).getJoiningDate());
        assertEquals("Bob Smith", result.get(2).getName());
        assertEquals("Charlie Brown", result.get(3).getName());
    }

    @Test
    public void testGetEmployees_WithPaginationSecondPage() {
        Pageable pageable = PageRequest.of(1, 10);
        Employee employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Jane Doe");
        employee2.setRole("Manager");

        List<Employee> employees = Arrays.asList(employee2);
        Page<Employee> pageOfEmployees = new PageImpl<>(employees, pageable, 11);

        when(employeeRepository.findAll(pageable)).thenReturn(pageOfEmployees);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals(11, result.getTotalElements());
        assertEquals(1, result.getNumber());
    }

    @Test
    public void testUpdateEmployee_RepositoryException() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setRole("Updated Role");
        updateDTO.setDepartmentId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, updateDTO));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testCreateEmployee_WithoutDepartmentAndNullValues() {
        EmployeeRequestDTO requestDTO = new EmployeeRequestDTO();
        requestDTO.setName("Bob Smith");
        requestDTO.setRole("Analyst");
        requestDTO.setSalary(40000.0);
        requestDTO.setJoiningDate(LocalDate.of(2023, 3, 20));
        requestDTO.setDepartmentId(null);

        Employee savedEmployee = new Employee();
        savedEmployee.setId(2L);
        savedEmployee.setName("Bob Smith");
        savedEmployee.setRole("Analyst");
        savedEmployee.setSalary(40000.0);
        savedEmployee.setJoiningDate(LocalDate.of(2023, 3, 20));
        savedEmployee.setDepartment(null);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);

        EmployeeResponseDTO result = employeeService.createEmployee(requestDTO);

        assertNotNull(result);
        assertEquals("Bob Smith", result.getName());
        assertNull(result.getDepartmentName());
        verify(departmentRepository, never()).findById(anyLong());
    }

    @Test
    public void testGetEmployeeById_UnexpectedException() {
        when(employeeRepository.findById(1L)).thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEmployeeById_DataAccessException() {
        when(employeeRepository.findById(1L)).thenThrow(new IllegalArgumentException("Invalid parameter"));

        assertThrows(IllegalArgumentException.class, () -> employeeService.getEmployeeById(1L));
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetEmployeeById_NullPointerException() {
        when(employeeRepository.findById(1L)).thenThrow(new NullPointerException("Null value encountered"));

        assertThrows(NullPointerException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    public void testGetEmployees_RepositoryThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> employeeService.getEmployees(null, pageable));
        verify(employeeRepository, times(1)).findAll(pageable);
    }

    @Test
    public void testGetEmployees_FindByDepartmentThrowsException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findByDepartmentName("IT", pageable))
                .thenThrow(new RuntimeException("Query execution failed"));

        assertThrows(RuntimeException.class, () -> employeeService.getEmployees("IT", pageable));
        verify(employeeRepository, times(1)).findByDepartmentName("IT", pageable);
    }

    @Test
    public void testGetEmployees_IllegalStateException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findAll(pageable)).thenThrow(new IllegalStateException("Invalid state"));

        assertThrows(IllegalStateException.class, () -> employeeService.getEmployees(null, pageable));
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate_RepositoryException() {
        when(employeeRepository.findAll()).thenThrow(new RuntimeException("Database connection failed"));

        assertThrows(RuntimeException.class, () -> employeeService.getAllEmployeesSortedByNameAndDate());
        verify(employeeRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate_DataAccessException() {
        when(employeeRepository.findAll()).thenThrow(new IllegalArgumentException("Invalid query parameter"));

        assertThrows(IllegalArgumentException.class, () -> employeeService.getAllEmployeesSortedByNameAndDate());
    }

    @Test
    public void testGetAllEmployeesSortedByNameAndDate_NullPointerException() {
        when(employeeRepository.findAll()).thenThrow(new NullPointerException("Null reference"));

        assertThrows(NullPointerException.class, () -> employeeService.getAllEmployeesSortedByNameAndDate());
    }

    @Test
    public void testMapToResponseDTO_ExceptionHandling() {
        Employee invalidEmployee = new Employee();
        invalidEmployee.setId(1L);
        invalidEmployee.setName("Test");
        invalidEmployee.setDepartment(null);

        List<Employee> employees = Arrays.asList(invalidEmployee);
        when(employeeRepository.findAll()).thenReturn(employees);

        assertDoesNotThrow(() -> employeeService.getAllEmployeesSortedByNameAndDate());
    }

    @Test
    public void testDeleteEmployee_UnexpectedRepositoryException() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doThrow(new RuntimeException("Connection timeout")).when(employeeRepository).delete(any(Employee.class));

        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(1L));
        verify(employeeRepository, times(1)).delete(employee);
    }

    @Test
    public void testUpdateEmployee_ExceptionDuringDepartmentRetrieval() {
        EmployeeUpdateRequestDTO updateDTO = new EmployeeUpdateRequestDTO();
        updateDTO.setName("Updated Name");
        updateDTO.setRole("Updated Role");
        updateDTO.setDepartmentId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Department service unavailable"));

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, updateDTO));
    }
}
