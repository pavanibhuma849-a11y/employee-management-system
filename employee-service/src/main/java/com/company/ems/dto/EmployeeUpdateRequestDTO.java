package com.company.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class EmployeeUpdateRequestDTO {

    private String name;

    private String role;

    @Positive(message = "salary must be positive")
    private Double salary;

    private LocalDate joiningDate;

    private Long departmentId;
    private java.util.Set<Long> projectIds;
}
