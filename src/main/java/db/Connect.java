package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Connect {

    private static final String URL      = "jdbc:postgresql://localhost:5432/customer-order";
    private static final String USERNAME = "postgres";
    private static final String PASSWORD = "Mbabazi12";

    private Connect() {}

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }
}
