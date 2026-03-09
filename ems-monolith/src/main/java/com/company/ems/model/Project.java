package com.company.ems.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Entity
@Getter
@Setter
public class Project extends BaseEntity {

    private String name;
    private Integer duration;

    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees;
}
