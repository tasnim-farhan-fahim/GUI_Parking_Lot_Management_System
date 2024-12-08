import java.sql.*;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DB_NAME = "parking_lot_system";
    private static final String USER = "root"; // Replace with your MySQL username
    private static final String PASSWORD = ""; // Replace with your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DB_NAME, USER, PASSWORD);
    }

    public static void initializeDatabase() {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");

            try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                 Statement stmt = conn.createStatement()) {

                // Create database if it doesn't exist
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + DB_NAME);

                // Use the database
                stmt.executeUpdate("USE " + DB_NAME);

                // Create tables if they don't exist
                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS slots (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        type VARCHAR(10) NOT NULL,
                        status VARCHAR(10) NOT NULL DEFAULT 'available'
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS vehicles (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        license_plate VARCHAR(20) NOT NULL,
                        type VARCHAR(10) NOT NULL,
                        entry_time DATETIME NOT NULL,
                        exit_time DATETIME,
                        slot_id INT,
                        FOREIGN KEY (slot_id) REFERENCES slots(id)
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS transactions (
                        id INT AUTO_INCREMENT PRIMARY KEY,
                        vehicle_id INT NOT NULL,
                        slot_id INT NOT NULL,
                        amount DECIMAL(10, 2) NOT NULL,
                        payment_time DATETIME NOT NULL,
                        FOREIGN KEY (vehicle_id) REFERENCES vehicles(id),
                        FOREIGN KEY (slot_id) REFERENCES slots(id)
                    );
                """);

                stmt.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS users (
                        user_id INT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(50) NOT NULL,
                        password VARCHAR(50) NOT NULL,
                        role ENUM('user', 'admin') NOT NULL
                    );
                """);

            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
