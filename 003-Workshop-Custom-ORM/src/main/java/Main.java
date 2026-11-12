import entities.User;
import orm.EntityManager;
import orm.MyConnector;

import java.sql.Connection;
import java.time.LocalDate;

public class Main {

    public static void main(String[] args) throws Exception {
        try (Connection connection = MyConnector.createConnection(
                System.getProperty("db.url", "jdbc:mysql://localhost:3306/custom_orm?createDatabaseIfNotExist=true"),
                System.getenv().getOrDefault("DB_USERNAME", "root"), System.getenv().getOrDefault("DB_PASSWORD", ""))) {

            EntityManager<User> userManager = new EntityManager<>(connection);
            userManager.doCreate(User.class);

            User user = new User("pesho", 25, LocalDate.now());
            userManager.persist(user);

            user.setAge(26);
            userManager.persist(user);

            userManager.find(User.class, "age > 18").forEach(System.out::println);
        }
    }
}
