package entities;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("car")
public class Car extends Vehicle {

    private Integer seats;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "plate_number_id", unique = true)
    private PlateNumber plateNumber;

    protected Car() {
    }

    public Car(String model, BigDecimal price, String fuelType, int seats, PlateNumber plateNumber) {
        super(model, price, fuelType);
        this.seats = seats;
        this.plateNumber = plateNumber;
    }

    public Integer getSeats() {
        return seats;
    }

    public PlateNumber getPlateNumber() {
        return plateNumber;
    }
}
