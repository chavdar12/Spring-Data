package com.example.springDataAutoMappingObjects.services;

import com.example.springDataAutoMappingObjects.models.DTO.ManagerDTO;

public interface EmployeeService {
    ManagerDTO findOne(Long id);

}
