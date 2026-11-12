package entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("bike")
public class Bike extends Vehicle {

    protected Bike() {
    }

    public Bike(String model, BigDecimal price) {
        super(model, price, "none");
    }
}
