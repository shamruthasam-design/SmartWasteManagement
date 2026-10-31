package dao;

import config.DBConnection;
import model.Staff;
import utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** Database access for the staff table. */
public class StaffDAO {
    private static final String SELECT =
            "SELECT s.id, s.name, s.phone, s.email, s.area, s.status, "
            + "(SELECT COUNT(*) FROM collection_requests r WHERE r.staff_id = s.id AND r.status <> 'Completed') AS open_tasks "
            + "FROM staff s ";

    public List<Staff> findAll() { return query(SELECT + "ORDER BY s.id"); }

    public List<Staff> findActive() { return query(SELECT + "WHERE s.status = 'Active' ORDER BY s.name"); }

    private List<Staff> query(String sql) {
        List<Staff> list = new ArrayList<>();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Staff(rs.getInt("id"), rs.getString("name"), rs.getString("phone"),
                        rs.getString("email"), rs.getString("area"), rs.getString("status"), rs.getInt("open_tasks")));
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
        return list;
    }

    public void insert(Staff s) {
        String sql = "INSERT INTO staff (name, phone, email, area, status) VALUES (?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.name());
            ps.setString(2, s.phone());
            ps.setString(3, s.email());
            ps.setString(4, s.area());
            ps.setString(5, s.status());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void update(Staff s) {
        String sql = "UPDATE staff SET name=?, phone=?, email=?, area=?, status=? WHERE id=?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, s.name());
            ps.setString(2, s.phone());
            ps.setString(3, s.email());
            ps.setString(4, s.area());
            ps.setString(5, s.status());
            ps.setInt(6, s.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void delete(int id) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM staff WHERE id=?")) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }
}
