package com.example.exerciseSpringDataAdvancedQuerying.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** H2 stand-in for the MySQL procedure in usp_get_books_count_by_author.sql. */
public final class H2Procedures {

    private H2Procedures() {
    }

    /** Runs on its own connection: DDL would commit the test's transaction and leak its data. */
    static void install(javax.sql.DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            statement.execute("CREATE ALIAS IF NOT EXISTS usp_get_books_count_by_author FOR '"
                              + H2Procedures.class.getName() + ".uspGetBooksCountByAuthor'");
        }
    }

    public static int uspGetBooksCountByAuthor(Connection connection, String firstName, String lastName) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT COUNT(*) FROM books b JOIN authors a ON a.id = b.author_id WHERE a.first_name = ? AND a.last_name = ?")) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            try (ResultSet rs = statement.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }
}
