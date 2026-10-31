package dao;

import config.DBConnection;
import utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database access for the activity_log table (shown as "Recent Activities"). */
public class ActivityDAO {

    public void log(String message) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO activity_log (message) VALUES (?)")) {
            ps.setString(1, message);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Could not write activity log: " + e.getMessage());
        }
    }

    /** Returns rows of {message, formatted time}, newest first. */
    public List<String[]> recent(int limit) {
        String sql = "SELECT message, DATE_FORMAT(created_at, '%d %b, %H:%i') FROM activity_log ORDER BY id DESC LIMIT ?";
        List<String[]> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(new String[]{rs.getString(1), rs.getString(2)});
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return list;
    }
}
