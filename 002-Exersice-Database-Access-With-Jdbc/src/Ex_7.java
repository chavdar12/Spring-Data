import java.sql.*;
import java.util.LinkedList;

public class Ex_7 {
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException {

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        statement = connection.prepareStatement("SELECT * FROM minions;");
        resultSet = statement.executeQuery();

        LinkedList<String> minions = new LinkedList();

        while (resultSet.next()) {
            minions.add(resultSet.getString("name"));
        }

        for (int i = 0; i < minions.size() / 2; i++) {
            if (i != minions.size() - 1 - i) {

                System.out.println(minions.get(i));
                System.out.println(minions.get(minions.size() - 1 - i));

            } else {

                System.out.println(minions.get(i));
            }
        }
    }
}