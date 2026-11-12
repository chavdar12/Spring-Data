package entities;

import jakarta.persistence.*;

@Entity
@Table(name = "plate_numbers")
public class PlateNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String number;

    protected PlateNumber() {
    }

    public PlateNumber(String number) {
        this.number = number;
    }

    public String getNumber() {
        return number;
    }
}
