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
        return employee(firstName, lastName, salary, manager, null);
    }

    private Employee employee(String firstName, String lastName, String salary, Employee manager, java.time.LocalDate birthday) {
        Employee employee = new Employee();
        employee.setBirthday(birthday);
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

    @Test
    void simpleMappingCopiesNameAndSalary() {
        Employee kevin = employee("Kevin", "Brown", "1500", null);

        EmployeeDTO dto = employeeService.findEmployee(kevin.getId());

        assertThat(dto.getFirstName()).isEqualTo("Kevin");
        assertThat(dto.getSalary()).isEqualByComparingTo("1500");
    }

    @Test
    void findAllManagersSkipsEmployeesWithoutSubordinates() {
        Employee boss = employee("Steve", "Jobbsen", "9000", null);
        employee("Stephen", "Bjorn", "4300", boss);
        entityManager.flush();
        entityManager.clear();

        assertThat(employeeService.findAllManagers()).singleElement()
                .satisfies(m -> assertThat(m.getSubordinates()).hasSize(1));
    }

    @Test
    void projectionBornBeforeYearBySalaryWithManagerLastName() {
        Employee boss = employee("Steve", "Jobbsen", "6000", null, java.time.LocalDate.of(1982, 3, 3));
        employee("Stephen", "Bjorn", "4300", boss, java.time.LocalDate.of(1985, 5, 5));
        employee("Young", "One", "9999", boss, java.time.LocalDate.of(1995, 1, 1));

        assertThat(employeeService.findAllBornBefore(1990)).extracting(Object::toString).containsExactly(
                String.format("Steve Jobbsen %.2f - Manager: [no manager]", 6000.0),
                String.format("Stephen Bjorn %.2f - Manager: Jobbsen", 4300.0));
    }
}
