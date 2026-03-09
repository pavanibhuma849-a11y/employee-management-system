package com.company.ems.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeTest {

    @Test
    public void testCompareToUsesSalary() {
        Employee lowerPaid = new Employee();
        lowerPaid.setName("Alice");
        lowerPaid.setRole("Developer");
        lowerPaid.setSalary(40000.0);
        lowerPaid.setJoiningDate(LocalDate.of(2022, 1, 1));

        Employee higherPaid = new Employee();
        higherPaid.setName("Bob");
        higherPaid.setRole("Senior Developer");
        higherPaid.setSalary(60000.0);
        higherPaid.setJoiningDate(LocalDate.of(2022, 1, 1));

        // This calls Employee.compareTo and exercises line 36
        int result = lowerPaid.compareTo(higherPaid);

        assertTrue(result < 0, "Employee with lower salary should be 'less than' higher salary");
    }

    @Test
    public void testInheritanceFromBaseEntity() {
        Employee employee = new Employee();
        employee.setId(100L);
        
        // Check if inherited field 'id' from BaseEntity is accessible
        assertEquals(100L, employee.getId(), "Employee should inherit 'id' from BaseEntity");
        
        // BaseEntity fields like createdAt/updatedAt are normally set by JPA, 
        // but we can test they exist and are accessible via Lombok getters/setters
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        employee.setCreatedAt(now);
        assertEquals(now, employee.getCreatedAt(), "Employee should inherit 'createdAt' from BaseEntity");
    }
}

