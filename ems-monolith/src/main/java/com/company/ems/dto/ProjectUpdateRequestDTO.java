package com.company.ems.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectUpdateRequestDTO {
    private String name;
    
    @Positive(message = "Duration must be positive")
    private Integer duration;
}
