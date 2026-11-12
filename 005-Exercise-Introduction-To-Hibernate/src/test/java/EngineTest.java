import entities.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EngineTest {

    private EntityManagerFactory factory;
    private EntityManager em;
    private Engine engine;
    private Employee guy;

    @BeforeEach
    void setUp() {
        factory = Persistence.createEntityManagerFactory("soft_uni", Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID(),
                "jakarta.persistence.jdbc.user", "sa",
                "hibernate.hbm2ddl.auto", "create-drop"));
        em = factory.createEntityManager();
        engine = new Engine(em);

        em.getTransaction().begin();
        Town sofia = new Town("Sofia");
        Town redmond = new Town("Redmond");
        em.persist(sofia);
        em.persist(redmond);
        Address a1 = new Address("1 Main St", redmond);
        Address a2 = new Address("2 Side St", redmond);
        em.persist(a1);
        em.persist(a2);

        Department rnd = new Department("Research and Development");
        Department marketing = new Department("Marketing");
        em.persist(rnd);
        em.persist(marketing);

        guy = new Employee("Guy", "Gilbert", "Tool Designer", rnd, new BigDecimal("40000"));
        Employee ken = new Employee("Ken", "Sanchez", "CEO", rnd, new BigDecimal("125500"));
        Employee kevin = new Employee("Kevin", "Brown", "Marketing Assistant", marketing, new BigDecimal("10000"));
        guy.setAddress(a1);
        ken.setAddress(a1);
        kevin.setAddress(a2);
        List.of(guy, ken, kevin).forEach(em::persist);

        for (int i = 1; i <= 11; i++) {
            Project project = new Project("Project %02d".formatted(i), "Description " + i, LocalDateTime.of(2000 + i, 1, 1, 0, 0), null);
            em.persist(project);
            if (i <= 2) guy.getProjects().add(project);
        }
        em.getTransaction().commit();
        em.clear();
    }

    @AfterEach
    void tearDown() {
        em.close();
        factory.close();
    }

    @Test
    void changeCasingUppercasesOnlyShortNames() {
        engine.run(2, "");
        em.clear();
        assertEquals(List.of("SOFIA", "Redmond"),
                em.createQuery("SELECT t.name FROM Town t ORDER BY t.id", String.class).getResultList());
    }

    @Test
    void containsEmployee() {
        assertEquals("Yes", engine.run(3, "Guy Gilbert"));
        assertEquals("No", engine.run(3, "Guy Ritchie"));
    }

    @Test
    void salaryOver50000AndDepartmentListing() {
        assertEquals("Ken", engine.run(4, ""));
        assertEquals(String.join(System.lineSeparator(),
                "Guy Gilbert from Research and Development - $40000.00",
                "Ken Sanchez from Research and Development - $125500.00"), engine.run(5, ""));
    }

    @Test
    void addNewAddressMovesEmployee() {
        engine.run(6, "Brown");
        em.clear();
        Employee kevin = em.createQuery("SELECT e FROM Employee e WHERE e.lastName = 'Brown'", Employee.class).getSingleResult();
        assertEquals("Vitoshka 15", kevin.getAddress().getText());
        assertEquals("There is no such employee.", engine.run(6, "Nobody"));
    }

    @Test
    void addressesWithEmployeeCount() {
        assertEquals(String.join(System.lineSeparator(),
                "1 Main St, Redmond - 2 employees",
                "2 Side St, Redmond - 1 employees"), engine.run(7, ""));
    }

    @Test
    void employeeWithProjectsAndLatestProjects() {
        assertEquals(String.join(System.lineSeparator(), "Guy Gilbert - Tool Designer", "\tProject 01", "\tProject 02"),
                engine.run(8, String.valueOf(guy.getId())));

        String latest = engine.run(9, "");
        assertTrue(latest.startsWith("Project name: Project 02"), "oldest project is left out");
        assertFalse(latest.contains("Project 01"));
    }

    @Test
    void increaseSalariesOnlyInListedDepartments() {
        assertEquals("Kevin Brown ($11200.00)", engine.run(10, ""));
    }

    @Test
    void employeesByFirstNameIsCaseInsensitive() {
        assertEquals("Ken Sanchez - CEO - ($125500.00)" + System.lineSeparator() + "Kevin Brown - Marketing Assistant - ($10000.00)",
                engine.run(11, "KE"));
    }

    @Test
    void maximumSalariesOutsideRange() {
        assertEquals("Research and Development 125500.00" + System.lineSeparator() + "Marketing 10000.00", engine.run(12, ""));
    }

    @Test
    void removeTownDeletesAddressesAndClearsEmployees() {
        assertEquals("2 addresses in Redmond deleted", engine.run(13, "Redmond"));
        em.clear();
        assertEquals(0L, em.createQuery("SELECT COUNT(a) FROM Address a", Long.class).getSingleResult());
        assertEquals(0L, em.createQuery("SELECT COUNT(e) FROM Employee e WHERE e.address IS NOT NULL", Long.class).getSingleResult());
        assertEquals("No such town.", engine.run(13, "Redmond"));
    }
}
