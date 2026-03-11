package com.company.ems.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
public class Employee extends BaseEntity implements Comparable<Employee> {

    @Column(unique = true, nullable = false)
    private String name;
    
    @Column(unique = true, nullable = false)
    private String email;
    private String role;
    private Double salary;
    private LocalDate joiningDate;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToMany
    @JoinTable(
        name = "employee_project",
        joinColumns = @JoinColumn(name = "employee_id"),
        inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private Set<Project> projects;

    @Override
    public int compareTo(Employee other) {
        return this.salary.compareTo(other.salary);
    }
}
