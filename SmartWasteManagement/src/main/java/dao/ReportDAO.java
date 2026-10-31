package dao;

import config.DBConnection;
import utils.AppException;
import utils.Constants;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Read-only aggregate queries used by the dashboard and the reports module. */
public class ReportDAO {

    /** Total kg per category (all four categories always present) from the given date. */
    public Map<String, Double> totalsByCategory(LocalDate from) {
        String sql = "SELECT c.name, COALESCE(SUM(w.quantity_kg), 0) FROM waste_categories c "
                + "LEFT JOIN waste_records w ON w.category_id = c.id AND w.record_date >= ? "
                + "GROUP BY c.id, c.name ORDER BY c.id";
        Map<String, Double> map = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getString(1), rs.getDouble(2));
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return map;
    }

    /** Total kg per calendar day from the given date (days without records are absent). */
    public Map<LocalDate, Double> totalsByDay(LocalDate from) {
        String sql = "SELECT record_date, SUM(quantity_kg) FROM waste_records WHERE record_date >= ? GROUP BY record_date";
        Map<LocalDate, Double> map = new HashMap<>();
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) map.put(rs.getDate(1).toLocalDate(), rs.getDouble(2));
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return map;
    }

    /** Number of completed collections per staff member. */
    public Map<String, Integer> completedByStaff() {
        String sql = "SELECT s.name, COUNT(r.id) FROM staff s "
                + "LEFT JOIN collection_requests r ON r.staff_id = s.id AND r.status = 'Completed' "
                + "GROUP BY s.id, s.name ORDER BY 2 DESC, s.name";
        Map<String, Integer> map = new LinkedHashMap<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) map.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return map;
    }

    /** Number of collection requests per status (Pending, Assigned, Completed). */
    public Map<String, Integer> collectionCounts() {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (String s : Constants.COLLECTION_STATUS) map.put(s, 0);
        String sql = "SELECT status, COUNT(*) FROM collection_requests GROUP BY status";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) map.put(rs.getString(1), rs.getInt(2));
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return map;
    }

    /** Sum of quantity for completed collections. */
    public double completedCollectionKg() {
        return scalar("SELECT COALESCE(SUM(quantity_kg),0) FROM collection_requests WHERE status='Completed'");
    }

    /** Sum of quantity for waste records that are already segregated. */
    public double segregatedKg() {
        return scalar("SELECT COALESCE(SUM(quantity_kg),0) FROM waste_records WHERE status='Segregated'");
    }

    /** Sum of quantity for a waste category (all records). */
    public double categoryKg(String category) {
        String sql = "SELECT COALESCE(SUM(w.quantity_kg),0) FROM waste_records w "
                + "JOIN waste_categories c ON c.id = w.category_id WHERE c.name = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    private double scalar(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getDouble(1);
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }
}
