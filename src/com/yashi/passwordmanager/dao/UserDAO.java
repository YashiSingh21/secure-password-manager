package com.yashi.passwordmanager.dao;

import com.yashi.passwordmanager.model.User;
import com.yashi.passwordmanager.util.AppException;
import com.yashi.passwordmanager.util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for the users table. Every method here is the ONLY
 * place raw SQL for users is written - the rest of the app talks to
 * UserService, which talks to this class. This separation (DAO pattern)
 * keeps SQL out of the business logic and UI layers.
 */
public class UserDAO {

    public User findByUsername(String username) throws AppException {
        String sql = "SELECT user_id, username, password_hash, salt, role FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null; // no such user
            }
        } catch (SQLException e) {
            throw new AppException("Could not look up user '" + username + "'.", e);
        }
    }

    public boolean usernameExists(String username) throws AppException {
        return findByUsername(username) != null;
    }

    public int createUser(User user) throws AppException {
        String sql = "INSERT INTO users (username, password_hash, salt, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPasswordHash());
            ps.setString(3, user.getSalt());
            ps.setString(4, user.getRole());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    return keys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new AppException("Could not create user '" + user.getUsername() + "'.", e);
        }
    }

    private User mapRow(ResultSet rs) throws SQLException {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("salt"),
                rs.getString("role")
        );
    }
}
