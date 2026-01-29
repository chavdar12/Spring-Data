import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_1 {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/soft_uni", "root", "ENTER PASSWORD");

        PreparedStatement statement = connection.prepareStatement("SELECT * FROM employees WHERE salary > ?");

        statement.setDouble(1, Double.parseDouble(reader.readLine()));

        ResultSet rs = statement.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString("first_name") + " " + rs.getString("last_name"));
        }
    }
}
