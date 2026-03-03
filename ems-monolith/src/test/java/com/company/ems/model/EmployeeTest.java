package com.company.ems.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}

