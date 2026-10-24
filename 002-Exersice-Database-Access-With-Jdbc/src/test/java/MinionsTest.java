import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MinionsTest {

    private static final String[] SCHEMA = {
            "CREATE TABLE towns (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), country VARCHAR(50))",
            "CREATE TABLE minions (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), age INT, town_id INT)",
            "CREATE TABLE villains (id INT AUTO_INCREMENT PRIMARY KEY, name VARCHAR(50), evilness_factor VARCHAR(20))",
            "CREATE TABLE minions_villains (minion_id INT, villain_id INT)",
    };

    private static TestDb db(String... data) throws SQLException {
        String[] statements = new String[SCHEMA.length + data.length];
        System.arraycopy(SCHEMA, 0, statements, 0, SCHEMA.length);
        System.arraycopy(data, 0, statements, SCHEMA.length, data.length);
        return new TestDb(statements);
    }

    private static List<String> query(Connection connection, String sql) throws SQLException {
        List<String> rows = new ArrayList<>();
        try (Statement statement = connection.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
            while (rs.next()) {
                rows.add(rs.getString(1));
            }
        }
        return rows;
    }

    /** Backs the usp_get_older procedure that Ex_9 calls. */
    public static void uspGetOlder(Connection connection, int id) throws SQLException {
        try (var statement = connection.prepareStatement("UPDATE minions SET age = age + 1 WHERE id = ?")) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    @Test
    void ex2PrintsVillainsWithMoreThan15Minions() throws Exception {
        StringBuilder links = new StringBuilder("INSERT INTO minions_villains VALUES (1, 2)");
        for (int minion = 1; minion <= 16; minion++) {
            links.append(", (").append(minion).append(", 1)");
        }
        try (TestDb db = db("INSERT INTO villains (name) VALUES ('Gru'), ('Victor')", links.toString())) {
            assertEquals("Gru 16\n", db.run("", () -> Ex_2.main(new String[0])));
        }
    }

    @Test
    void ex3PrintsVillainAndMinions() throws Exception {
        try (TestDb db = db("INSERT INTO villains (name) VALUES ('Gru')",
                "INSERT INTO minions (name, age) VALUES ('Bob', 14), ('Kevin', 10)",
                "INSERT INTO minions_villains VALUES (1, 1), (2, 1)")) {

            assertEquals("Villain: Gru\n1. Bob 14\n2. Kevin 10\n", db.run("1\n", () -> Ex_3.main(new String[0])));
            assertEquals("No villain with ID 7 exists in the database.", db.run("7\n", () -> Ex_3.main(new String[0])));
        }
    }

    @Test
    void ex4AddsMissingTownAndVillain() throws Exception {
        try (TestDb db = db()) {
            String out = db.run("Minion: Kevin 10 Sofia\nVillain: Gru\n", () -> Ex_4.main(new String[0]));

            assertEquals("Town Sofia was added to the database.\nVillain Gru was added to the database.\n"
                         + "Successfully added Kevin to be minion of Gru.", out);
            assertEquals(List.of("Kevin"), query(db.connection(),
                    "SELECT m.name FROM minions m JOIN minions_villains mv ON m.id = mv.minion_id "
                    + "JOIN villains v ON v.id = mv.villain_id JOIN towns t ON t.id = m.town_id "
                    + "WHERE v.name = 'Gru' AND t.name = 'Sofia' AND t.country IS NULL"));
        }
    }

    @Test
    void ex5UppercasesTownsOfCountry() throws Exception {
        try (TestDb db = db("INSERT INTO towns (name, country) VALUES ('Sofia', 'Bulgaria'), ('Plovdiv', 'Bulgaria'), "
                            + "('Berlin', 'Germany')")) {

            assertEquals("2 town names were affected.\n[SOFIA, PLOVDIV]\n",
                    db.run("Bulgaria\n", () -> Ex_5.main(new String[0])));
            assertEquals(List.of("SOFIA", "PLOVDIV", "Berlin"), query(db.connection(), "SELECT name FROM towns ORDER BY id"));
            assertEquals("No town names were affected.\n", db.run("Narnia\n", () -> Ex_5.main(new String[0])));
        }
    }

    @Test
    void ex6DeletesVillainAndReleasesMinions() throws Exception {
        try (TestDb db = db("INSERT INTO villains (name) VALUES ('Gru'), ('Victor')",
                "INSERT INTO minions_villains VALUES (1, 1), (2, 1), (3, 2)")) {

            assertEquals("Gru was deleted \n2 minions released", db.run("1\n", () -> Ex_6.main(new String[0])));
            assertEquals(List.of("Victor"), query(db.connection(), "SELECT name FROM villains"));
            assertEquals(List.of("2"), query(db.connection(), "SELECT villain_id FROM minions_villains"));
            assertEquals("No such villain was found\n", db.run("42\n", () -> Ex_6.main(new String[0])));
        }
    }

    @Test
    void ex7PrintsMinionsFirstLastAlternatingIncludingMiddle() throws Exception {
        try (TestDb db = db("INSERT INTO minions (name) VALUES ('A'), ('B'), ('C'), ('D'), ('E')")) {
            assertEquals("A\nE\nB\nD\nC\n", db.run("", () -> Ex_7.main(new String[0])));
        }
    }

    @Test
    void ex8IncrementsAgeAndLowercasesOnlySelectedIds() throws Exception {
        try (TestDb db = db("INSERT INTO minions (name, age) VALUES ('Bob', 10), ('Bob', 20), ('Stuart', 30)")) {
            assertEquals("bob 11\nBob 20\nstuart 31\n", db.run("1 3\n", () -> Ex_8.main(new String[0])));
        }
    }

    @Test
    void ex9CallsProcedureAndPrintsMinion() throws Exception {
        try (TestDb db = db("CREATE ALIAS usp_get_older FOR 'MinionsTest.uspGetOlder'",
                "INSERT INTO minions (name, age) VALUES ('Bob', 10)")) {

            assertEquals("Bob 11\n", db.run("1\n", () -> Ex_9.main(new String[0])));
        }
    }
}
