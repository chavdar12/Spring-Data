import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_2 {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/diablo", "root", "ENTER PASSWORD");

        PreparedStatement stmt = connection.prepareStatement("SELECT u.first_name, u.last_name, count(ug.game_id)" +
                "\r\n" + "FROM users as u\r\n" + "INNER JOIN users_games as ug ON u.id=ug.user_id\r\n" + "WHERE u" +
                ".user_name = ?;");

        String user = reader.readLine();

        stmt.setString(1, user);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String username = rs.getString("first_name");

            if (username != null) {
                System.out.printf("User: %s\r\n%s %s has played %d games\r\n", user, rs.getString("first_name"),
                        rs.getString("last_name"), rs.getInt(3));

            } else {
                System.out.println("No such user exists");
            }
        }
    }
}
