package com.company.ems.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProjectResponseDTO {
    private Long id;
    private String name;
    private Integer duration;
    private LocalDate startDate;
    private LocalDate endDate;
}
