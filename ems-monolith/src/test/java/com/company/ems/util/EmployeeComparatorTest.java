package com.company.ems.util;

import com.company.ems.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EmployeeComparatorTest {

    private EmployeeComparator employeeComparator;
    private Employee employee1;
    private Employee employee2;
    private Employee employee3;

    @BeforeEach
    public void setUp() {
        employeeComparator = new EmployeeComparator();

        employee1 = new Employee();
        employee1.setId(1L);
        employee1.setName("Alice Johnson");
        employee1.setRole("Developer");
        employee1.setSalary(55000.0);
        employee1.setJoiningDate(LocalDate.of(2021, 5, 10));

        employee2 = new Employee();
        employee2.setId(2L);
        employee2.setName("Bob Smith");
        employee2.setRole("Analyst");
        employee2.setSalary(45000.0);
        employee2.setJoiningDate(LocalDate.of(2022, 3, 15));

        employee3 = new Employee();
        employee3.setId(3L);
        employee3.setName("Alice Johnson");
        employee3.setRole("Manager");
        employee3.setSalary(65000.0);
        employee3.setJoiningDate(LocalDate.of(2020, 1, 20));
    }

    @Test
    public void testCompare_DifferentNames_FirstNameComesBeforeSecond() {
        int result = employeeComparator.compare(employee1, employee2);
        assertTrue(result < 0);
    }

    @Test
    public void testCompare_DifferentNames_SecondNameComesBeforeFirst() {
        int result = employeeComparator.compare(employee2, employee1);
        assertTrue(result > 0);
    }

    @Test
    public void testCompare_SameName_EarlierJoiningDateComesFirst() {
        int result = employeeComparator.compare(employee3, employee1);
        assertTrue(result < 0);
    }

    @Test
    public void testCompare_SameName_LaterJoiningDateComesSecond() {
        int result = employeeComparator.compare(employee1, employee3);
        assertTrue(result > 0);
    }

    @Test
    public void testCompare_IdenticalEmployees_ReturnsZero() {
        Employee employee4 = new Employee();
        employee4.setId(4L);
        employee4.setName("Alice Johnson");
        employee4.setRole("Developer");
        employee4.setSalary(55000.0);
        employee4.setJoiningDate(LocalDate.of(2021, 5, 10));

        int result = employeeComparator.compare(employee1, employee4);
        assertEquals(0, result);
    }

    @Test
    public void testCompare_SameNameSameJoiningDate_ReturnsZero() {
        Employee employee4 = new Employee();
        employee4.setId(4L);
        employee4.setName("Alice Johnson");
        employee4.setRole("Senior Developer");
        employee4.setSalary(75000.0);
        employee4.setJoiningDate(LocalDate.of(2021, 5, 10));

        int result = employeeComparator.compare(employee1, employee4);
        assertEquals(0, result);
    }

    @Test
    public void testCompare_NameComparison_CaseSensitive() {
        Employee lowerCaseEmployee = new Employee();
        lowerCaseEmployee.setId(5L);
        lowerCaseEmployee.setName("alice johnson");
        lowerCaseEmployee.setJoiningDate(LocalDate.of(2021, 5, 10));

        int result = employeeComparator.compare(employee1, lowerCaseEmployee);
        assertNotEquals(0, result);
    }

    @Test
    public void testSorting_MultipleEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employee2);
        employees.add(employee1);
        employees.add(employee3);

        Collections.sort(employees, employeeComparator);

        assertEquals("Alice Johnson", employees.get(0).getName());
        assertEquals(LocalDate.of(2020, 1, 20), employees.get(0).getJoiningDate());

        assertEquals("Alice Johnson", employees.get(1).getName());
        assertEquals(LocalDate.of(2021, 5, 10), employees.get(1).getJoiningDate());

        assertEquals("Bob Smith", employees.get(2).getName());
    }

    @Test
    public void testSorting_EmptyList() {
        List<Employee> employees = new ArrayList<>();
        Collections.sort(employees, employeeComparator);
        assertEquals(0, employees.size());
    }

    @Test
    public void testSorting_SingleEmployee() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);

        Collections.sort(employees, employeeComparator);

        assertEquals(1, employees.size());
        assertEquals("Alice Johnson", employees.get(0).getName());
    }

    @Test
    public void testSorting_EmployeesWithSameName() {
        List<Employee> employees = new ArrayList<>();
        employees.add(employee1);
        employees.add(employee3);

        Collections.sort(employees, employeeComparator);

        assertEquals("Alice Johnson", employees.get(0).getName());
        assertEquals(LocalDate.of(2020, 1, 20), employees.get(0).getJoiningDate());

        assertEquals("Alice Johnson", employees.get(1).getName());
        assertEquals(LocalDate.of(2021, 5, 10), employees.get(1).getJoiningDate());
    }

    @Test
    public void testCompare_NamePriority_OverJoiningDate() {
        Employee charlie = new Employee();
        charlie.setId(6L);
        charlie.setName("Charlie Brown");
        charlie.setJoiningDate(LocalDate.of(2019, 1, 1));

        List<Employee> employees = new ArrayList<>();
        employees.add(employee2);
        employees.add(charlie);
        employees.add(employee1);

        Collections.sort(employees, employeeComparator);

        assertEquals("Alice Johnson", employees.get(0).getName());
        assertEquals("Bob Smith", employees.get(1).getName());
        assertEquals("Charlie Brown", employees.get(2).getName());
    }

    @Test
    public void testCompare_WithDifferentSalaries_IgnoresSalary() {
        Employee highSalaryEmployee = new Employee();
        highSalaryEmployee.setId(7L);
        highSalaryEmployee.setName("Alice Johnson");
        highSalaryEmployee.setRole("Executive");
        highSalaryEmployee.setSalary(150000.0);
        highSalaryEmployee.setJoiningDate(LocalDate.of(2021, 5, 10));

        int result = employeeComparator.compare(employee1, highSalaryEmployee);
        assertEquals(0, result);
    }

    @Test
    public void testCompare_MultipleEmployeesWithVariations() {
        List<Employee> employees = new ArrayList<>();

        Employee emp1 = new Employee();
        emp1.setName("Zoe Adams");
        emp1.setJoiningDate(LocalDate.of(2023, 1, 1));
        employees.add(emp1);

        Employee emp2 = new Employee();
        emp2.setName("Alice Adams");
        emp2.setJoiningDate(LocalDate.of(2023, 6, 1));
        employees.add(emp2);

        Employee emp3 = new Employee();
        emp3.setName("Alice Adams");
        emp3.setJoiningDate(LocalDate.of(2023, 3, 1));
        employees.add(emp3);

        Employee emp4 = new Employee();
        emp4.setName("Bob Adams");
        emp4.setJoiningDate(LocalDate.of(2023, 2, 1));
        employees.add(emp4);

        Collections.sort(employees, employeeComparator);

        assertEquals("Alice Adams", employees.get(0).getName());
        assertEquals(LocalDate.of(2023, 3, 1), employees.get(0).getJoiningDate());

        assertEquals("Alice Adams", employees.get(1).getName());
        assertEquals(LocalDate.of(2023, 6, 1), employees.get(1).getJoiningDate());

        assertEquals("Bob Adams", employees.get(2).getName());
        assertEquals("Zoe Adams", employees.get(3).getName());
    }
}
