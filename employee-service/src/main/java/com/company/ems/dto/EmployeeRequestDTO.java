package com.company.ems.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Getter
@Setter
public class EmployeeRequestDTO {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "role is required")
    private String role;

    @NotNull(message = "salary is required")
    @Positive(message = "salary must be positive")
    private Double salary;

    @NotNull(message = "joiningDate is required")
    private LocalDate joiningDate;

    private Long departmentId;
    private java.util.Set<Long> projectIds;
}
