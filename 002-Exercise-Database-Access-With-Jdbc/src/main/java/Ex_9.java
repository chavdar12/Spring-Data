import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_9 {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection(System.getProperty("db.url", "jdbc:mysql://localhost:3306/minions_db"),
                System.getenv().getOrDefault("DB_USERNAME", "root"), System.getenv().getOrDefault("DB_PASSWORD", ""));

        int minionId = Integer.parseInt(reader.readLine());

        CallableStatement callableStatement = connection.prepareCall("CALL usp_get_older(?)");
        callableStatement.setInt(1, minionId);

        callableStatement.execute();

        PreparedStatement statement = connection.prepareStatement("SELECT name, age FROM minions WHERE id = ?");
        statement.setInt(1, minionId);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            System.out.printf("%s %d%n", resultSet.getString("name"), resultSet.getInt("age"));
        }
    }
}