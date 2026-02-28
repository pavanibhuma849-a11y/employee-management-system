package com.company.ems.service;

import com.company.ems.dto.ProjectRequestDTO;
import com.company.ems.dto.ProjectResponseDTO;
import com.company.ems.dto.ProjectUpdateRequestDTO;
import java.util.List;

public interface IProjectService {
    ProjectResponseDTO createProject(ProjectRequestDTO projectDTO);
    ProjectResponseDTO getProjectById(Long id);
    List<ProjectResponseDTO> getAllProjects();
    ProjectResponseDTO updateProject(Long id, ProjectUpdateRequestDTO projectDTO);
    void deleteProject(Long id);
}
