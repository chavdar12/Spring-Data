package com.example.springDataAutoMappingObjects.repositories;

import com.example.springDataAutoMappingObjects.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllBySubordinatesIsNotEmpty();

    List<Employee> findAllByBirthdayBeforeOrderBySalaryDesc(LocalDate date);
}
