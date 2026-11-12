package com.example.springDataAutoMappingObjects.services;

import com.example.springDataAutoMappingObjects.models.DTO.EmployeeDTO;
import com.example.springDataAutoMappingObjects.models.DTO.EmployeeWithManagerDTO;
import com.example.springDataAutoMappingObjects.models.DTO.ManagerDTO;

import java.util.List;

public interface EmployeeService {
    /** 1. Simple mapping. */
    EmployeeDTO findEmployee(Long id);

    /** 2. Advanced mapping: one manager with subordinates. */
    ManagerDTO findOne(Long id);

    /** 2. Advanced mapping: every employee who manages someone. */
    List<ManagerDTO> findAllManagers();

    /** 3. Projection: born before the year, by salary descending, with manager's last name. */
    List<EmployeeWithManagerDTO> findAllBornBefore(int year);
}
