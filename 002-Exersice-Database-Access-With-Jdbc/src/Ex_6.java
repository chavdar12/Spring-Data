import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_6 {
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        int villain_id = Integer.parseInt(reader.readLine());
        int minionsReleased;

        statement = connection.prepareStatement("DELETE FROM minions_villains WHERE villain_id = ?");

        statement.setInt(1, villain_id);
        minionsReleased = statement.executeUpdate();

        statement = connection.prepareStatement("SELECT * FROM villains WHERE id = ?");

        statement.setInt(1, villain_id);
        resultSet = statement.executeQuery();

        if (resultSet.next()) {

            System.out.printf("%s was deleted \n%d minions released", resultSet.getString("name"), minionsReleased);

            statement = connection.prepareStatement("DELETE FROM villains WHERE id = ?");
            statement.setInt(1, villain_id);
            statement.executeUpdate();

        } else {

            System.out.println("No such villain was found");
        }
    }
}