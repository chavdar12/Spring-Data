import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

/** Fresh in-memory H2 database (MySQL mode) that the exercises connect to through the db.url property. */
final class TestDb implements AutoCloseable {

    interface Program {
        void run() throws Exception;
    }

    private final Connection connection;

    TestDb(String... statements) throws SQLException {
        String url = "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=MySQL;DATABASE_TO_LOWER=TRUE";
        System.setProperty("db.url", url);
        // keeping this connection open keeps the in-memory database alive
        connection = DriverManager.getConnection(url, System.getenv().getOrDefault("DB_USERNAME", "root"),
                System.getenv().getOrDefault("DB_PASSWORD", ""));
        try (Statement statement = connection.createStatement()) {
            for (String sql : statements) {
                statement.execute(sql);
            }
        }
    }

    Connection connection() {
        return connection;
    }

    /** Runs the program with the given stdin and returns what it printed, with \n line endings. */
    String run(String stdin, Program program) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(new ByteArrayInputStream(stdin.getBytes(UTF_8)));
        System.setOut(new PrintStream(out, true, UTF_8));
        try {
            program.run();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return out.toString(UTF_8).replace("\r\n", "\n");
    }

    @Override
    public void close() throws SQLException {
        System.clearProperty("db.url");
        connection.close();
    }
}
