package orm;

import entities.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import orm.annotations.Column;
import orm.annotations.Entity;
import orm.annotations.Id;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class EntityManagerTest {

    private Connection connection;
    private EntityManager<User> users;

    @BeforeEach
    void setUp() throws Exception {
        connection = MyConnector.createConnection("jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE", "sa", "");
        users = new EntityManager<>(connection);
        users.doCreate(User.class);
    }

    @AfterEach
    void tearDown() throws Exception {
        connection.close();
    }

    @Test
    void persistInsertsThenUpdatesAndFindReadsBack() throws Exception {
        User user = new User("pesho", 25, LocalDate.of(2021, 6, 1));

        assertTrue(users.persist(user));
        assertTrue(user.getId() > 0, "generated id is written back");

        user.setAge(26);
        assertTrue(users.persist(user));

        List<User> all = new ArrayList<>();
        users.find(User.class).forEach(all::add);
        assertEquals(1, all.size());
        assertEquals("pesho", all.getFirst().getUsername());
        assertEquals(26, all.getFirst().getAge());
        assertEquals(LocalDate.of(2021, 6, 1), all.getFirst().getRegistration());
    }

    @Test
    void findFirstWithWhereAndDelete() throws Exception {
        users.persist(new User("young", 12, LocalDate.now()));
        User adult = new User("adult", 30, LocalDate.now());
        users.persist(adult);

        assertEquals("adult", users.findFirst(User.class, "age > 18").getUsername());
        assertNull(users.findFirst(User.class, "age > 100"));

        assertTrue(users.delete(adult));
        assertNull(users.findFirst(User.class, "age > 18"));
        assertEquals("young", users.findFirst(User.class).getUsername());
    }

    @Entity(name = "users")
    public static class UserWithEmail {
        @Id
        @Column(name = "id")
        private long id;
        @Column(name = "username")
        private String username;
        @Column(name = "email")
        private String email;

        public UserWithEmail() {
        }
    }

    @Test
    void doAlterAddsOnlyNewColumns() throws Exception {
        EntityManager<UserWithEmail> altered = new EntityManager<>(connection);
        altered.doAlter(UserWithEmail.class);
        altered.doAlter(UserWithEmail.class); // second run is a no-op

        UserWithEmail user = new UserWithEmail();
        user.username = "gosho";
        user.email = "gosho@abv.bg";
        altered.persist(user);

        assertEquals("gosho@abv.bg", altered.findFirst(UserWithEmail.class).email);
    }
}
