package com.example.springDataAutoMappingObjects.models.DTO;

import java.math.BigDecimal;

public class EmployeeWithManagerDTO extends BasicEmployeeDTO {

    private BigDecimal salary;

    // filled by ModelMapper from manager.lastName
    private String managerLastName;

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getManagerLastName() {
        return managerLastName;
    }

    public void setManagerLastName(String managerLastName) {
        this.managerLastName = managerLastName;
    }

    @Override
    public String toString() {
        return "%s %s %.2f - Manager: %s".formatted(getFirstName(), getLastName(), salary,
                managerLastName == null ? "[no manager]" : managerLastName);
    }
}
