package config;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Creates the database, tables and sample data automatically the first time the app runs.
 * If an old or incomplete database with the same name exists (different columns), it is recreated.
 */
public final class DatabaseInitializer {
    private DatabaseInitializer() { }

    public static void initialize() throws Exception {
        try (Connection con = DBConnection.getServerConnection(); Statement st = con.createStatement()) {
            st.execute("CREATE DATABASE IF NOT EXISTS " + DBConnection.DB_NAME);
            if (!schemaIsValid(st)) {
                // Old or incomplete setup: start fresh so the script can run cleanly
                st.execute("DROP DATABASE IF EXISTS " + DBConnection.DB_NAME);
                st.execute("CREATE DATABASE " + DBConnection.DB_NAME);
                st.execute("USE " + DBConnection.DB_NAME);
                runScript(st);
            }
        }
    }

    /** True only when all key columns of this project's tables exist. */
    private static boolean schemaIsValid(Statement st) throws Exception {
        String sql = "SELECT COUNT(*) FROM information_schema.columns WHERE table_schema='"
                + DBConnection.DB_NAME + "' AND ("
                + "(table_name='users' AND column_name='password_hash') OR "
                + "(table_name='staff' AND column_name='status') OR "
                + "(table_name='waste_categories' AND column_name='name') OR "
                + "(table_name='waste_records' AND column_name='quantity_kg') OR "
                + "(table_name='collection_requests' AND column_name='completed_at') OR "
                + "(table_name='activity_log' AND column_name='message'))";
        try (ResultSet rs = st.executeQuery(sql)) {
            rs.next();
            return rs.getInt(1) == 6;
        }
    }

    private static void runScript(Statement st) throws Exception {
        String sql;
        try (InputStream in = DatabaseInitializer.class.getResourceAsStream("/schema.sql")) {
            if (in == null) throw new IllegalStateException("schema.sql not found in resources.");
            sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
        }
        StringBuilder clean = new StringBuilder();
        for (String line : sql.split("\n")) {
            String t = line.trim();
            if (t.isEmpty() || t.startsWith("--")) continue;
            clean.append(line).append('\n');
        }
        for (String statement : clean.toString().split(";")) {
            if (!statement.isBlank()) st.execute(statement.trim());
        }
    }
}