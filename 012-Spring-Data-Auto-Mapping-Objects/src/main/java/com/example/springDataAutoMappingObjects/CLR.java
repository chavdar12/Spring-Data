package com.example.springDataAutoMappingObjects;

import com.example.springDataAutoMappingObjects.models.Employee;
import com.example.springDataAutoMappingObjects.repositories.EmployeeRepository;
import com.example.springDataAutoMappingObjects.services.EmployeeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

@Component
public class CLR implements CommandLineRunner {

    private final EmployeeService employeeService;
    private final EmployeeRepository employeeRepository;

    public CLR(EmployeeService employeeService, EmployeeRepository employeeRepository) {
        this.employeeService = employeeService;
        this.employeeRepository = employeeRepository;
    }

    @Override
    public void run(String... args) {
        if (employeeRepository.count() == 0) {
            seed();
        }

        employeeService.findAllManagers().forEach(manager -> {
            System.out.printf("%s %s | Employees: %d%n", manager.getFirstName(), manager.getLastName(), manager.getSubordinates().size());
            manager.getSubordinates().forEach(e -> System.out.printf("    - %s %s %.2f%n", e.getFirstName(), e.getLastName(), e.getSalary()));
        });

        employeeService.findAllBornBefore(1990).forEach(System.out::println);
    }

    private void seed() {
        Employee steve = employee("Steve", "Jobbsen", "6000", LocalDate.of(1982, 3, 3), null);
        employee("Stephen", "Bjorn", "4300", LocalDate.of(1985, 5, 5), steve);
        employee("Kirilyc", "Lefi", "4400", LocalDate.of(1993, 1, 1), steve);
        Employee carl = employee("Carl", "Kormac", "5000", LocalDate.of(1979, 9, 9), null);
        employee("Jurgen", "Straus", "1000.45", LocalDate.of(1988, 8, 8), carl);
        employee("Moni", "Kozinac", "2030.99", LocalDate.of(1991, 1, 1), carl);
        employee("Kopp", "Spidok", "2000.21", LocalDate.of(1987, 7, 7), carl);
    }

    private Employee employee(String firstName, String lastName, String salary, LocalDate birthday, Employee manager) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setSalary(new BigDecimal(salary));
        employee.setBirthday(birthday);
        employee.setManager(manager);
        return employeeRepository.save(employee);
    }
}
