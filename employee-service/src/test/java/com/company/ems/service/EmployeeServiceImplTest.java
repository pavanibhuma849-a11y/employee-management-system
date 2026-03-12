package com.company.ems.service;

import com.company.ems.dto.EmployeeRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.dto.EmployeeUpdateRequestDTO;
import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.DepartmentRepository;
import com.company.ems.repository.EmployeeRepository;
import com.company.ems.repository.ProjectRepository;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee;
    private Department department;
    private Project project;

    @BeforeEach
    public void setUp() {
        department = new Department();
        department.setId(10L);
        department.setName("Engineering");

        project = new Project();
        project.setId(20L);
        project.setName("AI Platform");
        project.setStartDate(LocalDate.now());
        project.setEndDate(LocalDate.now().plusMonths(6));

        employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setEmail("john.doe@company.com");
        employee.setRole("Developer");
        employee.setSalary(50000.0);
        employee.setJoiningDate(LocalDate.now());
        employee.setDepartment(department);
        employee.setProjects(new HashSet<>(Collections.singletonList(project)));
    }

    @Test
    public void testCreateEmployee_Success() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("John Doe");
        dto.setRole("Developer");
        dto.setSalary(50000.0);
        dto.setJoiningDate(LocalDate.now());

        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO result = employeeService.createEmployee(dto);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
        verify(employeeRepository).save(any(Employee.class));
        verify(emailService, times(1)).sendProjectAssignmentEmail(anyString(), anyString(), anyString(), any(LocalDate.class), any(LocalDate.class));
    }

    @Test
    public void testCreateEmployee_Exception() {
        EmployeeRequestDTO dto = new EmployeeRequestDTO();
        dto.setName("John Doe");

        when(employeeRepository.save(any(Employee.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> employeeService.createEmployee(dto));
    }

    @Test
    public void testGetEmployees_Exception() {
        Pageable pageable = PageRequest.of(0, 10);
        when(employeeRepository.findAll(pageable)).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> employeeService.getEmployees(null, pageable));
    }

    @Test
    public void testUpdateEmployee_DepartmentException() {
        EmployeeUpdateRequestDTO updateDto = new EmployeeUpdateRequestDTO();
        updateDto.setDepartmentId(10L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(10L)).thenThrow(new RuntimeException("DB Error"));

        assertThrows(RuntimeException.class, () -> employeeService.updateEmployee(1L, updateDto));
    }

    @Test
    public void testDeleteEmployee_Exception() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        doThrow(new RuntimeException("DB Error")).when(employeeRepository).delete(employee);

        assertThrows(RuntimeException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    public void testGetEmployeeById_Success() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        EmployeeResponseDTO result = employeeService.getEmployeeById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("John Doe", result.getName());
    }

    @Test
    public void testGetEmployeeById_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.getEmployeeById(1L));
    }

    @Test
    public void testDeleteEmployee_Success() {
        Employee employee = new Employee();
        employee.setId(1L);

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).delete(employee);
    }

    @Test
    public void testGetEmployees_WithoutDepartment() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        employee.setId(1L);
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));

        when(employeeRepository.findAll(pageable)).thenReturn(page);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(employeeRepository).findAll(pageable);
    }

    @Test
    public void testGetEmployees_WithDepartment() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        employee.setId(1L);
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));

        when(employeeRepository.findByDepartmentName("IT", pageable)).thenReturn(page);

        Page<EmployeeResponseDTO> result = employeeService.getEmployees("IT", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(employeeRepository).findByDepartmentName("IT", pageable);
    }

    @Test
    public void testGetEmployeesByDepartmentId() {
        Pageable pageable = PageRequest.of(0, 10);
        Employee employee = new Employee();
        employee.setId(1L);
        Page<Employee> page = new PageImpl<>(Collections.singletonList(employee));

        when(employeeRepository.findByDepartmentId(1L, pageable)).thenReturn(page);

        Page<EmployeeResponseDTO> result = employeeService.getEmployeesByDepartmentId(1L, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(employeeRepository).findByDepartmentId(1L, pageable);
    }

    @Test
    public void testDeleteEmployee_NotFound() {
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EmployeeNotFoundException.class, () -> employeeService.deleteEmployee(1L));
    }

    @Test
    public void testUpdateEmployee_FullUpdate() {
        Employee employee = new Employee();
        employee.setId(1L);
        
        Department department = new Department();
        department.setId(10L);
        department.setName("Engineering");

        Project project = new Project();
        project.setId(20L);
        project.setName("AI Platform");

        EmployeeUpdateRequestDTO updateDto = new EmployeeUpdateRequestDTO();
        updateDto.setName("New Name");
        updateDto.setRole("Senior Developer");
        updateDto.setSalary(75000.0);
        updateDto.setJoiningDate(LocalDate.now());
        updateDto.setDepartmentId(10L);
        updateDto.setProjectIds(new HashSet<>(Collections.singletonList(20L)));

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(10L)).thenReturn(Optional.of(department));
        when(projectRepository.findById(20L)).thenReturn(Optional.of(project));
        when(employeeRepository.save(any(Employee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("Senior Developer", result.getRole());
        assertEquals(75000.0, result.getSalary());
        assertEquals(department.getName(), result.getDepartmentName());
        assertTrue(result.getProjectNames().contains(project.getName()));
    }

    @Test
    public void testUpdateEmployee_Success() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("Old Name");

        EmployeeUpdateRequestDTO updateDto = new EmployeeUpdateRequestDTO();
        updateDto.setName("New Name");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        EmployeeResponseDTO result = employeeService.updateEmployee(1L, updateDto);

        assertNotNull(result);
        assertEquals("New Name", employee.getName());
    }

    @Test
    public void testGetEmployeesInTreeSet_NaturalOrderingBySalary() {
        Employee lowSalary = new Employee();
        lowSalary.setId(1L);
        lowSalary.setSalary(30000.0);

        Employee highSalary = new Employee();
        highSalary.setId(2L);
        highSalary.setSalary(90000.0);

        Employee midSalary = new Employee();
        midSalary.setId(3L);
        midSalary.setSalary(60000.0);

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(midSalary, highSalary, lowSalary));

        TreeSet<Employee> result = employeeService.getEmployeesInTreeSet();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals(lowSalary, result.first());
        assertEquals(highSalary, result.last());
        
        // Verify ascending order by salary
        Iterator<Employee> iterator = result.iterator();
        assertEquals(30000.0, iterator.next().getSalary());
        assertEquals(60000.0, iterator.next().getSalary());
        assertEquals(90000.0, iterator.next().getSalary());
    }

    @Test
    public void testGetEmployeeMapById_O1Lookup() {
        Employee emp1 = new Employee();
        emp1.setId(101L);
        emp1.setName("Emp 101");

        Employee emp2 = new Employee();
        emp2.setId(102L);
        emp2.setName("Emp 102");

        when(employeeRepository.findAll()).thenReturn(Arrays.asList(emp1, emp2));

        Map<Long, Employee> result = employeeService.getEmployeeMapById();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Emp 101", result.get(101L).getName());
        assertEquals("Emp 102", result.get(102L).getName());
        assertNull(result.get(103L));
    }
}
