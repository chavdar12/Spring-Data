import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Ex_9 {
    private static Connection connection;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        int minionId = Integer.parseInt(reader.readLine());

        CallableStatement callableStatement = connection.prepareCall("CALL usp_get_older(?)");
        callableStatement.setInt(1, minionId);

        callableStatement.executeQuery();
    }
}