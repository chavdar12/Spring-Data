package entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("plane")
public class Plane extends Vehicle {

    @Column(name = "passenger_capacity")
    private Integer passengerCapacity;

    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    protected Plane() {
    }

    public Plane(String model, BigDecimal price, String fuelType, int passengerCapacity, Company company) {
        super(model, price, fuelType);
        this.passengerCapacity = passengerCapacity;
        this.company = company;
    }

    public Integer getPassengerCapacity() {
        return passengerCapacity;
    }

    public Company getCompany() {
        return company;
    }
}
