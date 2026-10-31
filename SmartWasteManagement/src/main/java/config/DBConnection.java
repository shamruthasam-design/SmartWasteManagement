package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/** Central place for JDBC connections. Settings are read from src/main/resources/db.properties. */
public final class DBConnection {
    public static final String DB_NAME = "smart_waste_management";
    private static final String PARAMS = "useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = DBConnection.class.getResourceAsStream("/db.properties")) {
            if (in != null) PROPS.load(in);
        } catch (IOException e) {
            System.err.println("Could not read db.properties: " + e.getMessage());
        }
    }

    private DBConnection() { }

    private static String base() {
        return "jdbc:mysql://" + PROPS.getProperty("db.host", "localhost") + ":"
                + PROPS.getProperty("db.port", "3306") + "/";
    }

    /** Connection to the MySQL server without selecting a database (used to create the database). */
    public static Connection getServerConnection() throws SQLException {
        return DriverManager.getConnection(base() + "?" + PARAMS,
                PROPS.getProperty("db.user", "root"), PROPS.getProperty("db.password", ""));
    }

    /** Connection to the smart_waste_management database. */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(base() + DB_NAME + "?" + PARAMS,
                PROPS.getProperty("db.user", "root"), PROPS.getProperty("db.password", ""));
    }
}
