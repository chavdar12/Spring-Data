import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JdbcIntroTest {

    @Test
    void ex1PrintsEmployeesWithSalaryAboveInput() throws Exception {
        try (TestDb db = new TestDb(
                "CREATE TABLE employees (id INT PRIMARY KEY, first_name VARCHAR(50), last_name VARCHAR(50), salary DECIMAL(19, 4))",
                "INSERT INTO employees VALUES (1, 'Guy', 'Gilbert', 12500), (2, 'Ken', 'Sanchez', 125500), (3, 'Rob', 'Walters', 60100)")) {

            assertEquals("Ken Sanchez\nRob Walters\n", db.run("50000\n", () -> Ex_1.main(new String[0])));
        }
    }

    private static TestDb diablo() throws Exception {
        return new TestDb(
                "CREATE TABLE users (id INT PRIMARY KEY, user_name VARCHAR(50), first_name VARCHAR(50), last_name VARCHAR(50))",
                "CREATE TABLE users_games (user_id INT, game_id INT)",
                "INSERT INTO users VALUES (1, 'nakov', 'Svetlin', 'Nakov'), (2, 'idle', 'Idle', 'User')",
                "INSERT INTO users_games VALUES (1, 10), (1, 11), (1, 12)");
    }

    @Test
    void ex2PrintsGamesPlayedByUser() throws Exception {
        try (TestDb db = diablo()) {
            assertEquals("User: nakov\nSvetlin Nakov has played 3 games\n",
                    db.run("nakov\n", () -> Ex_2.main(new String[0])));
        }
    }

    @Test
    void ex2ReportsUnknownUser() throws Exception {
        try (TestDb db = diablo()) {
            assertEquals("No such user exists\n", db.run("ghost\n", () -> Ex_2.main(new String[0])));
        }
    }
}
