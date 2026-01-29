package com.example.springDataAutoMappingObjects.repositories;

import com.example.springDataAutoMappingObjects.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
}