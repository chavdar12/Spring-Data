import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Arrays;

public class Ex_8 {
    private static final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {


        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        Integer[] minionsId =
                Arrays.stream(reader.readLine().split("\\s+")).map(Integer::parseInt).toArray(Integer[]::new);

        incrementAge(minionsId);
        changeNameToLower(minionsId);
        printAllMinions();
    }

    private static void printAllMinions() throws SQLException {

        statement = connection.prepareStatement("SELECT * FROM minions");

        resultSet = statement.executeQuery();

        while (resultSet.next()) {

            System.out.printf("%s %d%n", resultSet.getString("name"), resultSet.getInt("age"));
        }
    }

    private static void changeNameToLower(Integer[] minionsId) throws SQLException {
        for (Integer id : minionsId) {

            statement = connection.prepareStatement("SELECT * FROM minions WHERE id = ?");
            statement.setInt(1, id);
            resultSet = statement.executeQuery();

            resultSet.next();

            statement = connection.prepareStatement("UPDATE minions SET name = LCASE(?) WHERE name = ?;");
            statement.setString(1, resultSet.getString("name"));
            statement.setString(2, resultSet.getString("name"));

            statement.executeUpdate();
        }

    }

    private static void incrementAge(Integer[] minionsId) throws SQLException {
        for (Integer id : minionsId) {

            statement = connection.prepareStatement("UPDATE minions SET age = age+1 WHERE id = ?;");
            statement.setInt(1, id);

            statement.executeUpdate();
        }
    }
}