import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Ex_5 {
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        String country = reader.readLine();
        resultSet = getTownsByCountry(country);

        int counter = 0;

        ArrayList<String> townsAffected = new ArrayList();

        while (resultSet.next()) {
            townsAffected.add(resultSet.getString("name").toUpperCase());

            statement = connection.prepareStatement("UPDATE towns SET name = UCASE(?) WHERE name = ?;");

            statement.setString(1, resultSet.getString("name"));
            statement.setString(2, resultSet.getString("name"));
            statement.executeUpdate();

            counter++;
        }

        if (counter == 0) {

            System.out.println("No town names were affected.");

        } else {

            System.out.printf("%d town names were affected.\n", counter);
            System.out.println(Arrays.toString(townsAffected.toArray()));

        }
    }

    private static ResultSet getTownsByCountry(String country) throws SQLException {

        statement = connection.prepareStatement("SELECT name FROM towns\r\n" + "WHERE country = ?;");
        statement.setString(1, country);


        return resultSet;
    }
}
