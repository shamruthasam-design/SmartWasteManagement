package dao;

import config.DBConnection;
import model.User;
import utils.AppException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** Database access for the users table. */
public class UserDAO {

    /** Returns the user with this username, or null when not found. */
    public User findByUsername(String username) {
        String sql = "SELECT id, username, password_hash, full_name, role FROM users WHERE username = ?";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }

    public void insert(User u) {
        String sql = "INSERT INTO users (username, password_hash, full_name, role) VALUES (?,?,?,?)";
        try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, u.username());
            ps.setString(2, u.passwordHash());
            ps.setString(3, u.fullName());
            ps.setString(4, u.role());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AppException("Database error: " + e.getMessage(), e);
        }
    }
}
