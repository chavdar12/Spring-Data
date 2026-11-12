import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void persistsHierarchyIntoOneTableWithRelations() {
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory("vehicles", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID(),
                "jakarta.persistence.jdbc.user", "sa",
                "hibernate.hbm2ddl.auto", "create-drop"));
             EntityManager em = factory.createEntityManager()) {

            Main.seed(em);
            em.clear();

            assertEquals(List.of("bike", "car", "truck", "plane"),
                    em.createQuery("SELECT v.type FROM Vehicle v ORDER BY v.id", String.class).getResultList());

            Car car = em.createQuery("SELECT c FROM Car c", Car.class).getSingleResult();
            assertEquals("CA1234AB", car.getPlateNumber().getNumber());

            Truck truck = em.createQuery("SELECT t FROM Truck t", Truck.class).getSingleResult();
            assertEquals("Ivan Ivanov", truck.getDrivers().iterator().next().getFullName());

            Company company = em.createQuery("SELECT c FROM Company c", Company.class).getSingleResult();
            assertEquals("Airbus A320", company.getPlanes().getFirst().getModel());
        }
    }
}
