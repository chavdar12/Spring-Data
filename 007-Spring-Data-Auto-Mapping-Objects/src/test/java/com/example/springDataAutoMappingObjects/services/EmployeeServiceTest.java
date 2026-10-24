package com.example.springDataAutoMappingObjects.services;

import com.example.springDataAutoMappingObjects.models.DTO.EmployeeDTO;
import com.example.springDataAutoMappingObjects.models.DTO.ManagerDTO;
import com.example.springDataAutoMappingObjects.models.Employee;
import com.example.springDataAutoMappingObjects.repositories.EmployeeRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

@DataJpaTest
@Import(EmployeeServiceImpl.class)
class EmployeeServiceTest {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private EmployeeRepository employeeRepository;
    @Autowired
    private EntityManager entityManager;

    private Employee employee(String firstName, String lastName, String salary, Employee manager) {
        Employee employee = new Employee();
        employee.setFirstName(firstName);
        employee.setLastName(lastName);
        employee.setSalary(new BigDecimal(salary));
        employee.setManager(manager);
        return employeeRepository.save(employee);
    }

    @Test
    void mapsManagerWithSubordinates() {
        Employee boss = employee("Steve", "Jobbsen", "9000", null);
        employee("Stephen", "Bjorn", "4300", boss);
        employee("Kirilyc", "Lefi", "4400", boss);
        entityManager.flush();
        entityManager.clear();

        ManagerDTO dto = employeeService.findOne(boss.getId());

        assertThat(dto.getFirstName()).isEqualTo("Steve");
        assertThat(dto.getLastName()).isEqualTo("Jobbsen");
        assertThat(dto.getSubordinates())
                .extracting(EmployeeDTO::getFirstName, EmployeeDTO::getLastName, e -> e.getSalary().intValue())
                .containsExactlyInAnyOrder(tuple("Stephen", "Bjorn", 4300), tuple("Kirilyc", "Lefi", 4400));
    }

    @Test
    void unknownEmployeeThrows() {
        assertThatThrownBy(() -> employeeService.findOne(404L)).isInstanceOf(NoSuchElementException.class);
    }
}
