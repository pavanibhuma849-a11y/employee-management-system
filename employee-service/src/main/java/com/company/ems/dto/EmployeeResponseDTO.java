package com.company.ems.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String role;
    private Double salary;
    private LocalDate joiningDate;
    private String departmentName;
    private java.util.Set<String> projectNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
