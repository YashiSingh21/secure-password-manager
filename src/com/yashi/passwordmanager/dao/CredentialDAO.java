package com.yashi.passwordmanager.dao;

import com.yashi.passwordmanager.model.Credential;
import com.yashi.passwordmanager.util.AppException;
import com.yashi.passwordmanager.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for the credentials table - performs the CRUD
 * operations (Create, Read, Update, Delete) via JDBC PreparedStatements.
 * PreparedStatements are used everywhere here instead of string-concatenated
 * SQL, which is what protects the app against SQL injection.
 */
public class CredentialDAO {

    public int addCredential(Credential c) throws AppException {
        String sql = "INSERT INTO credentials (user_id, site_name, site_username, site_password, notes) " +
                "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, c.getUserId());
            ps.setString(2, c.getSiteName());
            ps.setString(3, c.getSiteUsername());
            ps.setString(4, c.getSitePassword());
            ps.setString(5, c.getNotes());
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getInt(1);
            }
            return -1;
        } catch (SQLException e) {
            throw new AppException("Could not save credential for '" + c.getSiteName() + "'.", e);
        }
    }

    public List<Credential> getCredentialsForUser(int userId) throws AppException {
        String sql = "SELECT * FROM credentials WHERE user_id = ? ORDER BY site_name";
        List<Credential> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Could not load saved credentials.", e);
        }
    }

    public List<Credential> searchBySiteName(int userId, String keyword) throws AppException {
        String sql = "SELECT * FROM credentials WHERE user_id = ? AND site_name LIKE ? ORDER BY site_name";
        List<Credential> result = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapRow(rs));
                }
            }
            return result;
        } catch (SQLException e) {
            throw new AppException("Search failed for '" + keyword + "'.", e);
        }
    }

    public boolean updateCredential(Credential c) throws AppException {
        String sql = "UPDATE credentials SET site_name = ?, site_username = ?, site_password = ?, notes = ? " +
                "WHERE credential_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getSiteName());
            ps.setString(2, c.getSiteUsername());
            ps.setString(3, c.getSitePassword());
            ps.setString(4, c.getNotes());
            ps.setInt(5, c.getCredentialId());
            ps.setInt(6, c.getUserId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Could not update credential #" + c.getCredentialId() + ".", e);
        }
    }

    public boolean deleteCredential(int credentialId, int userId) throws AppException {
        String sql = "DELETE FROM credentials WHERE credential_id = ? AND user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, credentialId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AppException("Could not delete credential #" + credentialId + ".", e);
        }
    }

    private Credential mapRow(ResultSet rs) throws SQLException {
        Credential c = new Credential();
        c.setCredentialId(rs.getInt("credential_id"));
        c.setUserId(rs.getInt("user_id"));
        c.setSiteName(rs.getString("site_name"));
        c.setSiteUsername(rs.getString("site_username"));
        c.setSitePassword(rs.getString("site_password"));
        c.setNotes(rs.getString("notes"));
        c.setCreatedAt(rs.getTimestamp("created_at"));
        c.setUpdatedAt(rs.getTimestamp("updated_at"));
        return c;
    }
}
