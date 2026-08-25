package dao.util;

import config.DatabaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConnection {

    private static final DatabaseConfig CONFIG =
            new DatabaseConfig();

    private DBConnection() {
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(
                    "Driver MySQL introuvable: "
                            + e.getMessage()
            );
        }
    }

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(
                CONFIG.getUrl(),
                CONFIG.getUser(),
                CONFIG.getPassword()
        );
    }
}
