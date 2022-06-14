import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_3 {
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        int villain_id = Integer.parseInt(reader.readLine());

        if (!checkIfEntityExist(villain_id, "villains")) {
            System.out.printf("No villain with ID %d exists in the database.", villain_id);
            return;
        }

        System.out.printf("Villain: %s%n", getEntityById(villain_id, "villains"));

        getMinionAndAgeByVillainId(villain_id);
    }

    private static void getMinionAndAgeByVillainId(int id) throws SQLException {

        statement = connection.prepareStatement("SELECT m.name, m.age FROM minions AS m\r\n" + "JOIN minions_villains"
                                                + " mv on m.id = mv.minion_id\r\n" + "WHERE mv.villain_id = ?;");

        statement.setInt(1, id);

        resultSet = statement.executeQuery();

        int count = 1;

        while (resultSet.next()) {
            System.out.printf("%d. %s %s%n", count, resultSet.getString("name"), resultSet.getString("age"));
            count++;
        }
    }

    private static String getEntityById(int entityId, String table) throws SQLException {

        statement = connection.prepareStatement("SELECT name FROM " + table + " WHERE id = ?");

        statement.setInt(1, entityId);

        resultSet = statement.executeQuery();

        return resultSet.next() ? resultSet.getString("name") : null;
    }

    private static boolean checkIfEntityExist(int entityId, String table) throws SQLException {

        statement = connection.prepareStatement("SELECT * FROM " + table + " WHERE id = ?");

        statement.setInt(1, entityId);

        resultSet = statement.executeQuery();

        return resultSet.next();
    }
}
