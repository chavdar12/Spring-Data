import java.sql.*;

public class Ex_2 {
    private static Connection connection;

    public static void main(String[] args) throws SQLException {

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        PreparedStatement stmt = connection.prepareStatement("SELECT v.name, COUNT(mv.minion_id) AS `count`\r\n" +
                                                             "FROM villains AS v\r\n" + "JOIN minions_villains mv on v.id = mv.villain_id\r\n" + "GROUP BY v" +
                                                             ".name\r\n" + "HAVING `count` > 15\r\n" + "ORDER BY `count` DESC;");

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            System.out.println(rs.getString("name") + " " + rs.getString("count"));
        }
    }
}
