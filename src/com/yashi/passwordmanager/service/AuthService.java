package com.yashi.passwordmanager.service;

import com.yashi.passwordmanager.dao.UserDAO;
import com.yashi.passwordmanager.model.User;
import com.yashi.passwordmanager.util.AppException;
import com.yashi.passwordmanager.util.SecurityUtil;

/**
 * Business logic for registering and logging in users.
 * This is the layer that enforces rules (e.g. "username must be unique",
 * "password must be at least 6 characters") - the DAO layer below it
 * knows nothing about rules, only how to read/write rows.
 */
public class AuthService {

    private final UserDAO userDAO = new UserDAO();

    public User register(String username, String plainPassword) throws AppException {
        if (username == null || username.trim().isEmpty()) {
            throw new AppException("Username cannot be empty.");
        }
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new AppException("Master password must be at least 6 characters.");
        }
        if (userDAO.usernameExists(username)) {
            throw new AppException("Username '" + username + "' is already taken.");
        }

        String salt = SecurityUtil.generateSalt();
        String hash = SecurityUtil.hashPassword(plainPassword, salt);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(hash);
        user.setSalt(salt);
        user.setRole("USER");

        int newId = userDAO.createUser(user);
        user.setUserId(newId);
        return user;
    }

    /**
     * @return the authenticated User, or null if the username/password combo is wrong
     */
    public User login(String username, String plainPassword) throws AppException {
        User user = userDAO.findByUsername(username);
        if (user == null) {
            return null;
        }
        boolean ok = SecurityUtil.verifyPassword(plainPassword, user.getSalt(), user.getPasswordHash());
        return ok ? user : null;
    }
}
