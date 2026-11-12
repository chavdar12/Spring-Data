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

        connection = DriverManager.getConnection(System.getProperty("db.url", "jdbc:mysql://localhost:3306/minions_db"),
                System.getenv().getOrDefault("DB_USERNAME", "root"), System.getenv().getOrDefault("DB_PASSWORD", ""));

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

        addMinionToVillain(insertMinion(minionName, minionAge, minionTown), villainName);
        System.out.printf("Successfully added %s to be minion of %s.", minionName, villainName);

    }

    private static void addMinionToVillain(int minionId, String villainName) throws SQLException {

        // link by the new minion's id: names are not unique
        statement = connection.prepareStatement("INSERT INTO minions_villains (minion_id, villain_id) VALUES (?, " +
                                                "(SELECT id FROM villains WHERE name = ? LIMIT 1))");
        statement.setInt(1, minionId);
        statement.setString(2, villainName);
        statement.execute();
    }

    private static int insertMinion(String minionName, int minionAge, String minionTown) throws SQLException {

        statement =
                connection.prepareStatement("INSERT INTO minions (name, age,town_id) VALUES (?, ?, (SELECT id " +
                                            "FROM towns WHERE name = ? LIMIT 1))", Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, minionName);
        statement.setInt(2, minionAge);
        statement.setString(3, minionTown);
        statement.executeUpdate();
        resultSet = statement.getGeneratedKeys();
        resultSet.next();
        return resultSet.getInt(1);
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
        statement.setNull(2, Types.VARCHAR);
        statement.execute();
    }

    private static boolean checkIfEntityExistByName(String entityName, String tableName) throws SQLException {

        statement = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE name = ?");

        statement.setString(1, entityName);
        resultSet = statement.executeQuery();
        return resultSet.next();
    }
}
