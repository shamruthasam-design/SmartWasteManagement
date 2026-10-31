package dao;

import config.DBConnection;
import model.CollectionRequest;
import utils.AppException;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/** Database access for the collection_requests table. */
public class CollectionDAO {
    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");

    public List<CollectionRequest> findAll() {
        String sql = "SELECT r.id, r.location, c.name, r.quantity_kg, r.staff_id, s.name, "
                + "r.collection_date, r.status, r.completed_at "
                + "FROM collection_requests r JOIN waste_categories c ON c.id = r.category_id "
                + "LEFT JOIN staff s ON s.id = r.staff_id "
                + "ORDER BY r.collection_date DESC, r.id DESC";
        List<CollectionRequest> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int staffId = rs.getInt(5);
                Integer sid = rs.wasNull() ? null : staffId;
                Timestamp done = rs.getTimestamp(9);
                list.add(new CollectionRequest(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getDouble(4),
                        sid, rs.getString(6), rs.getDate(7).toLocalDate(), rs.getString(8),
                        done == null ? "-" : done.toLocalDateTime().format(STAMP)));
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    public void insert(String location, String category, double qty, Integer staffId, LocalDate date, String status) {
        String sql = "INSERT INTO collection_requests (location, category_id, quantity_kg, staff_id, collection_date, status) "
                + "VALUES (?, (SELECT id FROM waste_categories WHERE name = ?), ?, ?, ?, ?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, location);
            ps.setString(2, category);
            ps.setDouble(3, qty);
            if (staffId == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, staffId);
            ps.setDate(5, Date.valueOf(date));
            ps.setString(6, status);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void update(int id, String location, String category, double qty, Integer staffId, LocalDate date, String status) {
        String sql = "UPDATE collection_requests SET location=?, category_id=(SELECT id FROM waste_categories WHERE name = ?), "
                + "quantity_kg=?, staff_id=?, collection_date=?, status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, location);
            ps.setString(2, category);
            ps.setDouble(3, qty);
            if (staffId == null) ps.setNull(4, Types.INTEGER); else ps.setInt(4, staffId);
            ps.setDate(5, Date.valueOf(date));
            ps.setString(6, status);
            ps.setInt(7, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void updateStatus(int id, String status) {
        String sql = "UPDATE collection_requests SET status=?, completed_at = IF(?='Completed', NOW(), NULL) WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void assign(int requestId, int staffId) {
        String sql = "UPDATE collection_requests SET staff_id=?, status='Assigned', completed_at=NULL WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, staffId);
            ps.setInt(2, requestId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM collection_requests WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }
}
