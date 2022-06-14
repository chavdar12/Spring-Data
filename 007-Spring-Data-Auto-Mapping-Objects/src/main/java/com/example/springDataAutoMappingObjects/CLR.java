package com.example.springDataAutoMappingObjects;

import com.example.springDataAutoMappingObjects.services.EmployeeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CLR implements CommandLineRunner {

    private final EmployeeService employeeService;

    public CLR(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public void run(String... args) throws Exception {
        //ManagerDTO dto = this.employeeService.findOne(1L);

    }
}
