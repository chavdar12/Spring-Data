import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Ex_4 {
    private static Connection connection;
    private static PreparedStatement statement;
    private static ResultSet resultSet;

    public static void main(String[] args) throws SQLException, IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/minions_db", "root", "ENTER PASSWORD");

        String[] minionParameters = reader.readLine().split("\\s+");

        String minionName = minionParameters[1];
        int minionAge = Integer.parseInt(minionParameters[2]);
        String minionTown = minionParameters[3];

        String villainName = reader.readLine().split("\\s+")[1];

        if (!checkIfEntityExistByName(minionTown, "towns")) {
            insertEntityInTowns(minionTown);
            System.out.printf("Town %s was added to the database.%n", minionTown);
        }
        if (!checkIfEntityExistByName(villainName, "villains")) {
            insertEntityInVillains(villainName);
            System.out.printf("Villain %s was added to the database.%n", villainName);
        }

        insertMinion(minionName, minionAge, minionTown);
        addMinionToVillain(minionName, villainName);
        System.out.printf("Successfully added %s to be minion of %s.", minionName, villainName);

    }

    private static void addMinionToVillain(String minionName, String villainName) throws SQLException {

        statement = connection.prepareStatement("INSERT INTO minions_villains (minion_id, villain_id) VALUES (" +
                                                "(SELECT" + " id FROM minions WHERE name = ? " + "LIMIT 1), (SELECT id FROM villains WHERE name = ?))" +
                                                ";");
        statement.setString(1, minionName);
        statement.setString(2, villainName);
        statement.execute();
    }

    private static void insertMinion(String minionName, int minionAge, String minionTown) throws SQLException {

        statement =
                connection.prepareStatement("INSERT INTO minions (name, age,town_id) VALUES (?, ?, (SELECT id " +
                                            "FROM towns WHERE name = ?))");
        statement.setString(1, minionName);
        statement.setInt(2, minionAge);
        statement.setString(3, minionTown);
        statement.execute();
    }

    private static void insertEntityInVillains(String villainName) throws SQLException {
        statement = connection.prepareStatement("INSERT INTO villains (name, evilness_factor) values(?, ?)");
        statement.setString(1, villainName);
        statement.setString(2, "evil");
        statement.execute();
    }

    private static void insertEntityInTowns(String minionTown) throws SQLException {

        statement = connection.prepareStatement("INSERT INTO towns (name, country) values(?, ?)");

        statement.setString(1, minionTown);
        statement.setString(2, "NULL");
        statement.execute();
    }

    private static boolean checkIfEntityExistByName(String entityName, String tableName) throws SQLException {

        statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE name = ?");

        statement.setString(1, entityName);
        resultSet = statement.executeQuery();
        return resultSet.next();
    }
}
