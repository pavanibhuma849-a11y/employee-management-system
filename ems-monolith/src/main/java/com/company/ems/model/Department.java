package com.company.ems.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Department extends BaseEntity implements Comparable<Department> {

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;

    @Override
    public int compareTo(Department other) {
        return this.name.compareTo(other.name);
    }
}
