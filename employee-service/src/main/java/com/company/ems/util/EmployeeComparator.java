package com.company.ems.util;

import com.company.ems.model.Employee;
import java.util.Comparator;

public class EmployeeComparator implements Comparator<Employee> {
    @Override
    public int compare(Employee e1, Employee e2) {
        int nameCompare = e1.getName().compareTo(e2.getName());
        if (nameCompare != 0) {
            return nameCompare;
        }
        return e1.getJoiningDate().compareTo(e2.getJoiningDate());
    }
}
