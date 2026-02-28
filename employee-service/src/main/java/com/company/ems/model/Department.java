package com.company.ems.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Getter
@Setter
public class Department extends BaseEntity {

    private String name;

    @OneToMany(mappedBy = "department")
    private List<Employee> employees;
}
