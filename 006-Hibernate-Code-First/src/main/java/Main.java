import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
        Map<String, String> overrides = new HashMap<>();
        if (System.getenv("DB_USERNAME") != null) overrides.put("jakarta.persistence.jdbc.user", System.getenv("DB_USERNAME"));
        if (System.getenv("DB_PASSWORD") != null) overrides.put("jakarta.persistence.jdbc.password", System.getenv("DB_PASSWORD"));

        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("vehicles", overrides);
             EntityManager em = factory.createEntityManager()) {
            seed(em);
            em.createQuery("SELECT v FROM Vehicle v ORDER BY v.id", Vehicle.class).getResultList()
                    .forEach(v -> System.out.printf("%s %s %.2f%n", v.getType(), v.getModel(), v.getPrice()));
        }
    }

    static void seed(EntityManager em) {
        em.getTransaction().begin();
        Company airline = new Company("Bulgaria Air");
        em.persist(airline);

        Driver driver = new Driver("Ivan Ivanov");
        em.persist(driver);
        Truck truck = new Truck("Volvo FH", new BigDecimal("120000"), "diesel", 40);
        truck.getDrivers().add(driver);

        em.persist(new Bike("BMX", new BigDecimal("300")));
        em.persist(new Car("Golf", new BigDecimal("25000"), "petrol", 5, new PlateNumber("CA1234AB")));
        em.persist(truck);
        em.persist(new Plane("Airbus A320", new BigDecimal("98000000"), "kerosene", 180, airline));
        em.getTransaction().commit();
    }
}
