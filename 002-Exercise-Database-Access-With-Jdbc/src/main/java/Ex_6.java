import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_6 {

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        Connection connection = DriverManager.getConnection(System.getProperty("db.url", "jdbc:mysql://localhost:3306/minions_db"),
                System.getenv().getOrDefault("DB_USERNAME", "root"), System.getenv().getOrDefault("DB_PASSWORD", ""));

        int villainId = Integer.parseInt(reader.readLine());

        PreparedStatement select = connection.prepareStatement("SELECT name FROM villains WHERE id = ?");
        select.setInt(1, villainId);
        ResultSet resultSet = select.executeQuery();

        if (!resultSet.next()) {
            System.out.println("No such villain was found");
            return;
        }
        String villainName = resultSet.getString("name");

        // both deletes succeed or neither does
        connection.setAutoCommit(false);
        try {
            PreparedStatement releaseMinions = connection.prepareStatement("DELETE FROM minions_villains WHERE villain_id = ?");
            releaseMinions.setInt(1, villainId);
            int minionsReleased = releaseMinions.executeUpdate();

            PreparedStatement deleteVillain = connection.prepareStatement("DELETE FROM villains WHERE id = ?");
            deleteVillain.setInt(1, villainId);
            deleteVillain.executeUpdate();

            connection.commit();
            System.out.printf("%s was deleted%n%d minions released%n", villainName, minionsReleased);
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }
}
