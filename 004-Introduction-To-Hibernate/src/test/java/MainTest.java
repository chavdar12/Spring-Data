import entities.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void savesAndReadsStudentsThroughTheConfiguredMapping() {
        Configuration configuration = new Configuration().configure()
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:" + UUID.randomUUID())
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop");

        try (SessionFactory factory = configuration.buildSessionFactory(); Session session = factory.openSession()) {
            Student teo = Main.save(session, new Student("Teo"));
            Main.save(session, new Student("Ani"));
            session.clear();

            assertEquals("Teo", session.find(Student.class, teo.getId()).getName());
            assertEquals(List.of("Teo", "Ani"), Main.listAll(session).stream().map(Student::getName).toList());
        }
    }
}
