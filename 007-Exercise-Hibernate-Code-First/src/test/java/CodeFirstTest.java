import billspayment.BankAccount;
import billspayment.CreditCard;
import billspayment.User;
import gringotts.WizardDeposit;
import hospital.Diagnose;
import hospital.Medicament;
import hospital.Patient;
import hospital.Visitation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.junit.jupiter.api.Test;
import sales.Customer;
import sales.Product;
import sales.Sale;
import sales.StoreLocation;
import university.Course;
import university.Student;
import university.Teacher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CodeFirstTest {

    private static void inUnit(String unit, Consumer<EntityManager> work) {
        try (EntityManagerFactory factory = Persistence.createEntityManagerFactory(unit, Map.of(
                "jakarta.persistence.jdbc.url", "jdbc:h2:mem:" + UUID.randomUUID(),
                "jakarta.persistence.jdbc.user", "sa",
                "hibernate.hbm2ddl.auto", "create-drop"));
             EntityManager em = factory.createEntityManager()) {
            em.getTransaction().begin();
            work.accept(em);
            em.getTransaction().commit();
            em.clear();
            check(unit, em);
        }
    }

    private static void check(String unit, EntityManager em) {
        String jpql = switch (unit) {
            case "gringotts" -> "SELECT COUNT(w) FROM WizardDeposit w";
            case "sales" -> "SELECT COUNT(s) FROM Sale s WHERE s.product.name = 'Milk' AND s.storeLocation.locationName = 'Sofia'";
            case "university" -> "SELECT COUNT(c) FROM Course c JOIN c.students s WHERE c.teacher.lastName = 'Nakov'";
            case "hospital" -> "SELECT COUNT(m) FROM Patient p JOIN p.medicaments m";
            default -> "SELECT COUNT(b) FROM BillingDetail b WHERE b.owner.email = 'pesho@abv.bg'";
        };
        assertEquals(unit.equals("billspayment") ? 2L : 1L, em.createQuery(jpql, Long.class).getSingleResult(), unit);
    }

    @Test
    void gringotts() {
        inUnit("gringotts", em -> {
            WizardDeposit deposit = new WizardDeposit();
            deposit.setLastName("Potter");
            deposit.setAge(17);
            deposit.setDepositAmount(new BigDecimal("1000.50"));
            em.persist(deposit);
        });
    }

    @Test
    void sales() {
        inUnit("sales", em -> {
            Product milk = new Product();
            milk.setName("Milk");
            Customer customer = new Customer();
            customer.setName("Ivan");
            StoreLocation sofia = new StoreLocation();
            sofia.setLocationName("Sofia");
            Sale sale = new Sale();
            sale.setProduct(milk);
            sale.setCustomer(customer);
            sale.setStoreLocation(sofia);
            sale.setDate(LocalDateTime.now());
            for (Object entity : new Object[]{milk, customer, sofia, sale}) em.persist(entity);
        });
    }

    @Test
    void university() {
        inUnit("university", em -> {
            Teacher teacher = new Teacher();
            teacher.setFirstName("Svetlin");
            teacher.setLastName("Nakov");
            Course course = new Course();
            course.setName("Spring Data");
            course.setTeacher(teacher);
            Student student = new Student();
            student.setFirstName("Ani");
            student.setLastName("Ivanova");
            student.getCourses().add(course);
            for (Object entity : new Object[]{teacher, course, student}) em.persist(entity);
        });
    }

    @Test
    void hospital() {
        inUnit("hospital", em -> {
            Patient patient = new Patient();
            patient.setFirstName("Ivan");
            patient.setLastName("Petrov");
            Medicament aspirin = new Medicament();
            aspirin.setName("Aspirin");
            patient.getMedicaments().add(aspirin);
            Visitation visitation = new Visitation();
            visitation.setDate(LocalDateTime.now());
            visitation.setPatient(patient);
            Diagnose flu = new Diagnose();
            flu.setName("Flu");
            flu.setPatient(patient);
            for (Object entity : new Object[]{aspirin, patient, visitation, flu}) em.persist(entity);
        });
    }

    @Test
    void billsPayment() {
        inUnit("billspayment", em -> {
            User user = new User();
            user.setFirstName("Pesho");
            user.setLastName("Peshev");
            user.setEmail("pesho@abv.bg");
            user.setPassword("secret");
            CreditCard card = new CreditCard();
            card.setNumber("4111111111111111");
            card.setCardType("VISA");
            card.setExpirationMonth(12);
            card.setExpirationYear(2030);
            card.setOwner(user);
            BankAccount account = new BankAccount();
            account.setNumber("BG80BNBG96611020345678");
            account.setBankName("BNB");
            account.setSwiftCode("BNBGBGSD");
            account.setOwner(user);
            for (Object entity : new Object[]{user, card, account}) em.persist(entity);
        });
    }
}
