import entities.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        Configuration configuration = new Configuration().configure();
        // credentials from the environment win over hibernate.cfg.xml
        if (System.getenv("DB_USERNAME") != null) configuration.setProperty("hibernate.connection.username", System.getenv("DB_USERNAME"));
        if (System.getenv("DB_PASSWORD") != null) configuration.setProperty("hibernate.connection.password", System.getenv("DB_PASSWORD"));

        try (SessionFactory sessionFactory = configuration.buildSessionFactory();
             Session session = sessionFactory.openSession()) {

            Student student = save(session, new Student("Teo"));
            System.out.println("Saved: " + student);
            System.out.println("Loaded: " + session.find(Student.class, student.getId()));
            listAll(session).forEach(System.out::println);
        }
    }

    static Student save(Session session, Student student) {
        session.beginTransaction();
        session.persist(student);
        session.getTransaction().commit();
        return student;
    }

    static List<Student> listAll(Session session) {
        return session.createQuery("FROM Student ORDER BY id", Student.class).getResultList();
    }
}
