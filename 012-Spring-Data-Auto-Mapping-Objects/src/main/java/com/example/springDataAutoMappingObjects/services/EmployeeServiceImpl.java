package com.example.springDataAutoMappingObjects.services;

import com.example.springDataAutoMappingObjects.models.DTO.EmployeeDTO;
import com.example.springDataAutoMappingObjects.models.DTO.EmployeeWithManagerDTO;
import com.example.springDataAutoMappingObjects.models.DTO.ManagerDTO;
import com.example.springDataAutoMappingObjects.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final ModelMapper mapper = new ModelMapper();

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public EmployeeDTO findEmployee(Long id) {
        return mapper.map(this.employeeRepository.findById(id).orElseThrow(), EmployeeDTO.class);
    }

    @Override
    public ManagerDTO findOne(Long id) {
        return mapper.map(this.employeeRepository.findById(id).orElseThrow(), ManagerDTO.class);
    }

    @Override
    public List<ManagerDTO> findAllManagers() {
        return this.employeeRepository.findAllBySubordinatesIsNotEmpty().stream()
                .map(manager -> mapper.map(manager, ManagerDTO.class))
                .toList();
    }

    @Override
    public List<EmployeeWithManagerDTO> findAllBornBefore(int year) {
        return this.employeeRepository.findAllByBirthdayBeforeOrderBySalaryDesc(LocalDate.of(year, 1, 1)).stream()
                .map(employee -> mapper.map(employee, EmployeeWithManagerDTO.class))
                .toList();
    }
}
