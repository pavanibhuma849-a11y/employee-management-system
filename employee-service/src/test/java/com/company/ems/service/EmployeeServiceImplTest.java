package com.company.ems.service;

import com.company.ems.exception.EmployeeNotFoundException;
import com.company.ems.model.Employee;
import com.company.ems.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

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
