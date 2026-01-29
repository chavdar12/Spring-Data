package com.example.springDataAutoMappingObjects.services;

import com.example.springDataAutoMappingObjects.models.DTO.EmployeeDTO;
import com.example.springDataAutoMappingObjects.models.DTO.ManagerDTO;
import com.example.springDataAutoMappingObjects.models.Employee;
import com.example.springDataAutoMappingObjects.repositories.EmployeeRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private final EmployeeRepository employeeRepository;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @Override
    public ManagerDTO findOne(Long id) {
        ModelMapper mapper = new ModelMapper();
        mapper.addMappings(new PropertyMap<Employee, EmployeeDTO>() {
            @Override
            protected void configure() {
                map().setSalary(source.getSalary());
            }
        });

        return mapper.map(this.employeeRepository.findById(id).orElseThrow(), ManagerDTO.class);
    }
}
