package com.company.ems.service;

import com.company.ems.dto.DepartmentRequestDTO;
import com.company.ems.dto.DepartmentResponseDTO;
import com.company.ems.dto.DepartmentUpdateRequestDTO;
import com.company.ems.dto.EmployeeResponseDTO;
import com.company.ems.exception.DepartmentNotFoundException;
import com.company.ems.exception.DuplicateResourceException;
import com.company.ems.exception.ResourceConflictException;
import com.company.ems.model.Department;
import com.company.ems.model.Employee;
import com.company.ems.model.Project;
import com.company.ems.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private Department department;
    private DepartmentRequestDTO departmentRequestDTO;
    private DepartmentResponseDTO departmentResponseDTO;

    @BeforeEach
    public void setUp() {
        department = new Department();
        department.setId(1L);
        department.setName("IT");

        departmentRequestDTO = new DepartmentRequestDTO();
        departmentRequestDTO.setName("IT");

        departmentResponseDTO = new DepartmentResponseDTO();
        departmentResponseDTO.setId(1L);
        departmentResponseDTO.setName("IT");
    }

    @Test
    public void testCreateDepartment_Success() {
        when(departmentRepository.save(any(Department.class))).thenReturn(department);

        DepartmentResponseDTO result = departmentService.createDepartment(departmentRequestDTO);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        assertEquals(1L, result.getId());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testCreateDepartment_WithDifferentName() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("HR");

        Department hrDepartment = new Department();
        hrDepartment.setId(2L);
        hrDepartment.setName("HR");

        when(departmentRepository.save(any(Department.class))).thenReturn(hrDepartment);

        DepartmentResponseDTO result = departmentService.createDepartment(requestDTO);

        assertNotNull(result);
        assertEquals("HR", result.getName());
        assertEquals(2L, result.getId());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testGetDepartmentById_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        DepartmentResponseDTO result = departmentService.getDepartmentById(1L);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        assertEquals(1L, result.getId());
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetDepartmentById_DepartmentNotFound() {
        when(departmentRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(999L));
        verify(departmentRepository, times(1)).findById(999L);
    }

    @Test
    public void testGetDepartmentById_WithZeroId() {
        when(departmentRepository.findById(0L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.getDepartmentById(0L));
        verify(departmentRepository, times(1)).findById(0L);
    }

    @Test
    public void testGetAllDepartments_Success() {
        Department department1 = new Department();
        department1.setId(1L);
        department1.setName("IT");

        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("HR");

        Department department3 = new Department();
        department3.setId(3L);
        department3.setName("Finance");

        List<Department> departments = Arrays.asList(department1, department2, department3);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("Finance", result.get(0).getName());
        assertEquals("HR", result.get(1).getName());
        assertEquals("IT", result.get(2).getName());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllDepartments_EmptyList() {
        when(departmentRepository.findAll()).thenReturn(new java.util.ArrayList<>());

        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(0, result.size());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testGetAllDepartments_SingleDepartment() {
        List<Department> departments = Arrays.asList(department);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("IT", result.get(0).getName());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateDepartment_Success() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("Information Technology");

        Department updatedDepartment = new Department();
        updatedDepartment.setId(1L);
        updatedDepartment.setName("Information Technology");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        DepartmentResponseDTO result = departmentService.updateDepartment(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Information Technology", result.getName());
        assertEquals(1L, result.getId());
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testUpdateDepartment_DepartmentNotFound() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("IT");

        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.updateDepartment(999L, updateDTO));
        verify(departmentRepository, times(1)).findById(999L);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    public void testUpdateDepartment_SameName() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("IT");

        Department updatedDepartment = new Department();
        updatedDepartment.setId(1L);
        updatedDepartment.setName("IT");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDepartment);

        DepartmentResponseDTO result = departmentService.updateDepartment(1L, updateDTO);

        assertNotNull(result);
        assertEquals("IT", result.getName());
        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testDeleteDepartment_Success() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        departmentService.deleteDepartment(1L);

        verify(departmentRepository, times(1)).findById(1L);
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    public void testDeleteDepartment_DepartmentNotFound() {
        when(departmentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(DepartmentNotFoundException.class, () -> departmentService.deleteDepartment(999L));
        verify(departmentRepository, times(1)).findById(999L);
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    @Test
    public void testDeleteDepartment_VerifyCorrectDepartmentDeleted() {
        Department department2 = new Department();
        department2.setId(2L);
        department2.setName("HR");

        when(departmentRepository.findById(2L)).thenReturn(Optional.of(department2));

        departmentService.deleteDepartment(2L);

        verify(departmentRepository, times(1)).findById(2L);
        verify(departmentRepository, times(1)).delete(department2);
    }

    @Test
    public void testCreateDepartment_RepositoryException() {
        when(departmentRepository.save(any(Department.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> departmentService.createDepartment(departmentRequestDTO));
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testGetAllDepartments_RepositoryException() {
        when(departmentRepository.findAll()).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> departmentService.getAllDepartments());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateDepartment_RepositoryException() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("Finance");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenThrow(new RuntimeException("Save failed"));

        assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(1L, updateDTO));
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testDeleteDepartment_RepositoryException() {
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        doThrow(new RuntimeException("Delete failed")).when(departmentRepository).delete(any(Department.class));

        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, times(1)).delete(department);
    }

    @Test
    public void testGetAllDepartments_MultipleDepartmentsWithDifferentNames() {
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("IT");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setName("HR");

        Department dept3 = new Department();
        dept3.setId(3L);
        dept3.setName("Finance");

        Department dept4 = new Department();
        dept4.setId(4L);
        dept4.setName("Operations");

        List<Department> departments = Arrays.asList(dept1, dept2, dept3, dept4);

        when(departmentRepository.findAll()).thenReturn(departments);

        List<DepartmentResponseDTO> result = departmentService.getAllDepartments();

        assertNotNull(result);
        assertEquals(4, result.size());
        assertEquals("Finance", result.get(0).getName());
        assertEquals("HR", result.get(1).getName());
        assertEquals("IT", result.get(2).getName());
        assertEquals("Operations", result.get(3).getName());
        verify(departmentRepository, times(1)).findAll();
    }

    @Test
    public void testUpdateDepartment_ToSameNameDifferentDepartment() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("Finance");

        Department updatedDept = new Department();
        updatedDept.setId(1L);
        updatedDept.setName("Finance");

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));
        when(departmentRepository.save(any(Department.class))).thenReturn(updatedDept);

        DepartmentResponseDTO result = departmentService.updateDepartment(1L, updateDTO);

        assertNotNull(result);
        assertEquals("Finance", result.getName());
        verify(departmentRepository, times(1)).save(any(Department.class));
    }

    @Test
    public void testCreateDepartment_WithSpecialCharactersInName() {
        DepartmentRequestDTO requestDTO = new DepartmentRequestDTO();
        requestDTO.setName("R&D-Center");

        Department savedDept = new Department();
        savedDept.setId(5L);
        savedDept.setName("R&D-Center");

        when(departmentRepository.save(any(Department.class))).thenReturn(savedDept);

        DepartmentResponseDTO result = departmentService.createDepartment(requestDTO);

        assertNotNull(result);
        assertEquals("R&D-Center", result.getName());
        assertEquals(5L, result.getId());
    }

    @Test
    public void testGetDepartmentById_WithDifferentId() {
        Department dept = new Department();
        dept.setId(3L);
        dept.setName("Finance");

        when(departmentRepository.findById(3L)).thenReturn(Optional.of(dept));

        DepartmentResponseDTO result = departmentService.getDepartmentById(3L);

        assertNotNull(result);
        assertEquals("Finance", result.getName());
        assertEquals(3L, result.getId());
        verify(departmentRepository, times(1)).findById(3L);
    }

    @Test
    public void testGetDepartmentById_UnexpectedException() {
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Database connection error"));

        assertThrows(RuntimeException.class, () -> departmentService.getDepartmentById(1L));
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetDepartmentById_DataAccessException() {
        when(departmentRepository.findById(1L)).thenThrow(new IllegalArgumentException("Invalid parameter"));

        assertThrows(IllegalArgumentException.class, () -> departmentService.getDepartmentById(1L));
        verify(departmentRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetDepartmentById_NullPointerException() {
        when(departmentRepository.findById(1L)).thenThrow(new NullPointerException("Null value encountered"));

        assertThrows(NullPointerException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    public void testGetDepartmentById_IllegalStateException() {
        when(departmentRepository.findById(1L)).thenThrow(new IllegalStateException("Invalid state"));

        assertThrows(IllegalStateException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    public void testMapToResponseDTO_ExceptionHandling() {
        Department testDept = new Department();
        testDept.setId(1L);
        testDept.setName("Test");

        List<Department> departments = Arrays.asList(testDept);
        when(departmentRepository.findAll()).thenReturn(departments);

        assertDoesNotThrow(() -> departmentService.getAllDepartments());
    }

    @Test
    public void testUpdateDepartment_UnexpectedException() {
        DepartmentUpdateRequestDTO updateDTO = new DepartmentUpdateRequestDTO();
        updateDTO.setName("Updated IT");

        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Database unavailable"));

        assertThrows(RuntimeException.class, () -> departmentService.updateDepartment(1L, updateDTO));
    }

    @Test
    public void testDeleteDepartment_UnexpectedException() {
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Connection timeout"));

        assertThrows(RuntimeException.class, () -> departmentService.deleteDepartment(1L));
    }

    @Test
    public void testCreateDepartment_DuplicateName() {
        when(departmentRepository.existsByName("IT")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> departmentService.createDepartment(departmentRequestDTO));
        verify(departmentRepository, never()).save(any(Department.class));
    }

    @Test
    public void testGetAllDepartments_Paginated_Success() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> page = new PageImpl<>(Arrays.asList(department));
        when(departmentRepository.findAll(pageable)).thenReturn(page);

        Page<DepartmentResponseDTO> result = departmentService.getAllDepartments(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(departmentRepository).findAll(pageable);
    }

    @Test
    public void testGetAllDepartments_Paginated_Exception() {
        Pageable pageable = PageRequest.of(0, 10);
        when(departmentRepository.findAll(pageable)).thenThrow(new RuntimeException("DB error"));

        assertThrows(RuntimeException.class, () -> departmentService.getAllDepartments(pageable));
    }

    @Test
    public void testDeleteDepartment_WithEmployees() {
        Employee employee = new Employee();
        employee.setName("John Doe");
        department.setEmployees(Arrays.asList(employee));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        assertThrows(ResourceConflictException.class, () -> departmentService.deleteDepartment(1L));
        verify(departmentRepository, never()).delete(any(Department.class));
    }

    @Test
    public void testGetEmployeesByDepartment_Success() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setName("John Doe");
        employee.setRole("Developer");
        employee.setSalary(50000.0);
        employee.setDepartment(department);
        
        Project project = new Project();
        project.setName("Project A");
        employee.setProjects(new HashSet<>(Arrays.asList(project)));
        
        department.setEmployees(Arrays.asList(employee));

        when(departmentRepository.findById(1L)).thenReturn(Optional.of(department));

        List<EmployeeResponseDTO> result = departmentService.getEmployeesByDepartment(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("John Doe", result.get(0).getName());
        assertEquals("Project A", result.get(0).getProjectNames().iterator().next());
        assertEquals("IT", result.get(0).getDepartmentName());
    }

    @Test
    public void testGetEmployeesByDepartment_Exception() {
        when(departmentRepository.findById(1L)).thenThrow(new RuntimeException("Unexpected error"));

        assertThrows(RuntimeException.class, () -> departmentService.getEmployeesByDepartment(1L));
    }

    @Test
    public void testMapToResponseDTO_Exception() {
        Department mockDept = mock(Department.class);
        when(mockDept.getId()).thenThrow(new RuntimeException("Mapping failed"));
        
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(mockDept));

        assertThrows(RuntimeException.class, () -> departmentService.getDepartmentById(1L));
    }

    @Test
    public void testMapToEmployeeResponseDTO_Exception() {
        Employee mockEmp = mock(Employee.class);
        when(mockEmp.getId()).thenThrow(new RuntimeException("Mapping failed"));
        
        Department dept = new Department();
        dept.setEmployees(Arrays.asList(mockEmp));
        
        when(departmentRepository.findById(1L)).thenReturn(Optional.of(dept));

        assertThrows(RuntimeException.class, () -> departmentService.getEmployeesByDepartment(1L));
    }
}
