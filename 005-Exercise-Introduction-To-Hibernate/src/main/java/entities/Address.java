package entities;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    private Integer id;

    @Column(name = "address_text")
    private String text;

    @ManyToOne
    @JoinColumn(name = "town_id")
    private Town town;

    @OneToMany(mappedBy = "address")
    private Set<Employee> employees = new HashSet<>();

    protected Address() {
    }

    public Address(String text, Town town) {
        this.text = text;
        this.town = town;
    }

    public Integer getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public Town getTown() {
        return town;
    }

    public Set<Employee> getEmployees() {
        return employees;
    }
}
