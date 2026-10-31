package dao;

import config.DBConnection;
import model.WasteRecord;
import utils.AppException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/** Database access for the waste_records table. */
public class WasteDAO {

    public List<WasteRecord> findAll() {
        String sql = "SELECT w.id, c.name, w.quantity_kg, w.record_date, w.status, w.notes "
                + "FROM waste_records w JOIN waste_categories c ON c.id = w.category_id "
                + "ORDER BY w.record_date DESC, w.id DESC";
        List<WasteRecord> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String notes = rs.getString(6);
                list.add(new WasteRecord(rs.getInt(1), rs.getString(2), rs.getDouble(3),
                        rs.getDate(4).toLocalDate(), rs.getString(5), notes == null ? "" : notes));
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    public void insert(String category, double qty, LocalDate date, String status, String notes) {
        String sql = "INSERT INTO waste_records (category_id, quantity_kg, record_date, status, notes) "
                + "VALUES ((SELECT id FROM waste_categories WHERE name = ?), ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setDouble(2, qty);
            ps.setDate(3, Date.valueOf(date));
            ps.setString(4, status);
            ps.setString(5, notes);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void update(int id, String category, double qty, LocalDate date, String status, String notes) {
        String sql = "UPDATE waste_records SET category_id = (SELECT id FROM waste_categories WHERE name = ?), "
                + "quantity_kg = ?, record_date = ?, status = ?, notes = ? WHERE id = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, category);
            ps.setDouble(2, qty);
            ps.setDate(3, Date.valueOf(date));
            ps.setString(4, status);
            ps.setString(5, notes);
            ps.setInt(6, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE waste_records SET status=? WHERE id=?")) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM waste_records WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }
}
