package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceConflictException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("Engineering");
        department.setEmployees(new ArrayList<>());

        requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("Engineering");
    }

    @Test
    void testCreateDepartment_Success() {
        when(departmentRepository.existsByName("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentResponseDTO response = departmentService.createDepartment(requestDTO);

        assertNotNull(response);
        assertEquals("Engineering", response.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    void testCreateDepartment_DuplicateName() {
        when(departmentRepository.existsByName("Engineering")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departmentService.createDepartment(requestDTO));
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    void testGetDepartmentById_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentResponseDTO response = departmentService.getDepartmentById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Engineering", response.getName());
    }

    @Test
    void testGetDepartmentById_NotFound() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    void testUpdateDepartment_Success() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("R&D");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentResponseDTO response = departmentService.updateDepartment(1L, updateDTO);

        assertNotNull(response);
        assertEquals("R&D", department.getName());
        verify(departmentRepository, times(1)).save(department);
    }

    @Test
    void testDeleteDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    void testDeleteDepartment_ConflictWithEmployees() {
        Employee employee = new Employee();
        department.getEmployees().add(employee);

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThrows(ResourceConflictException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, never()).delete(any(Department.class));
    }
}
