package com.company.ems.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
public class Project extends BaseEntity {

    @Column(unique = true, nullable = false)
    private String name;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;

    @ManyToMany(mappedBy = "projects")
    private Set<Employee> employees;
}
