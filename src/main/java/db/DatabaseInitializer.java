package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Creates the required database tables if they do not already exist.
 * Call DatabaseInitializer.initialize() once at application startup.
 */
public class DatabaseInitializer {

    private DatabaseInitializer() {}

    public static void initialize() {
        try (Connection conn = Connect.getConnection();
             Statement stmt = conn.createStatement()) {

            // --- Products table ---
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS products (" +
                "  product_id   INT PRIMARY KEY," +
                "  product_name VARCHAR(200) NOT NULL," +
                "  price        DOUBLE PRECISION NOT NULL," +
                "  stock        INT NOT NULL DEFAULT 0," +
                "  category     VARCHAR(100) NOT NULL DEFAULT 'General'" +
                ")"
            );

            // --- Customers table (phone is UNIQUE for phone-based login) ---
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS customers (" +
                "  customer_id INT PRIMARY KEY," +
                "  name        VARCHAR(200) NOT NULL," +
                "  email       VARCHAR(200) NOT NULL UNIQUE," +
                "  phone       VARCHAR(50)  NOT NULL UNIQUE," +
                "  address     VARCHAR(300) NOT NULL" +
                ")"
            );

            // --- Orders table ---
            // status stores the OrderStatus enum name as a string
            // (PENDING, PROCESSING, DELIVERED, CANCELLED)
            stmt.executeUpdate(
                "CREATE TABLE IF NOT EXISTS orders (" +
                "  order_id       INT PRIMARY KEY," +
                "  customer_id    INT NOT NULL REFERENCES customers(customer_id)," +
                "  product_id     INT NOT NULL REFERENCES products(product_id)," +
                "  quantity       INT NOT NULL," +
                "  payment_method VARCHAR(100) NOT NULL," +
                "  status         VARCHAR(50)  NOT NULL DEFAULT 'PENDING'" +
                ")"
            );

            System.out.println("[DB] Tables verified / created successfully.");

        } catch (SQLException e) {
            System.out.println("[DB] ERROR: Could not initialize database tables.");
            System.out.println("[DB] " + e.getMessage());
        }
    }
}
