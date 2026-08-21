package com.yashi.passwordmanager.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Central place that hands out JDBC connections to the MySQL database.
 * Kept as a single utility class so connection details live in one spot.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/password_manager_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "appuser";
    private static final String PASSWORD = "AppPass123!";

    // Load the driver once when the class is first used.
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC driver not found on classpath.", e);
        }
    }

    private DBConnection() {
        // utility class - no instances
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
