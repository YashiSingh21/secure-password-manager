package com.yashi.passwordmanager.model;

/**
 * Represents an application user (someone who logs into the password manager).
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String salt;
    private String role; // "ADMIN" or "USER"

    public User() {
    }

    public User(int userId, String username, String passwordHash, String salt, String role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.salt = salt;
        this.role = role;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(role);
    }

    @Override
    public String toString() {
        return "User{id=" + userId + ", username='" + username + "', role='" + role + "'}";
    }
}
