package com.yashi.passwordmanager.service;

import com.yashi.passwordmanager.dao.CredentialDAO;
import com.yashi.passwordmanager.model.Credential;
import com.yashi.passwordmanager.util.AppException;
import com.yashi.passwordmanager.util.SecurityUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for managing saved credentials. Encryption/decryption of the
 * site password happens ONLY in this layer - the DAO stores/reads whatever
 * string it's given, and the UI never sees ciphertext directly.
 */
public class CredentialService {

    private final CredentialDAO credentialDAO = new CredentialDAO();

    public Credential saveCredential(int userId, String siteName, String siteUsername,
                                      String plainSitePassword, String notes) throws AppException {
        if (siteName == null || siteName.trim().isEmpty()) {
            throw new AppException("Site name is required.");
        }
        if (plainSitePassword == null || plainSitePassword.isEmpty()) {
            throw new AppException("Site password cannot be empty.");
        }

        String encrypted = SecurityUtil.encrypt(plainSitePassword);
        Credential c = new Credential(0, userId, siteName, siteUsername, encrypted, notes);
        int id = credentialDAO.addCredential(c);
        c.setCredentialId(id);
        return c;
    }

    /** Returns credentials with the password still encrypted (safe for list views). */
    public List<Credential> listCredentials(int userId) throws AppException {
        return credentialDAO.getCredentialsForUser(userId);
    }

    public List<Credential> search(int userId, String keyword) throws AppException {
        return credentialDAO.searchBySiteName(userId, keyword);
    }

    /** Decrypts a single credential's password - call this only when the user asks to reveal it. */
    public String revealPassword(Credential c) {
        return SecurityUtil.decrypt(c.getSitePassword());
    }

    public void updateCredential(int userId, int credentialId, String siteName, String siteUsername,
                                  String newPlainPassword, String notes) throws AppException {
        String encrypted = SecurityUtil.encrypt(newPlainPassword);
        Credential c = new Credential(credentialId, userId, siteName, siteUsername, encrypted, notes);
        boolean updated = credentialDAO.updateCredential(c);
        if (!updated) {
            throw new AppException("No credential found with id " + credentialId + " for this user.");
        }
    }

    public void deleteCredential(int userId, int credentialId) throws AppException {
        boolean deleted = credentialDAO.deleteCredential(credentialId, userId);
        if (!deleted) {
            throw new AppException("No credential found with id " + credentialId + " for this user.");
        }
    }

    /** Simple strength check used before saving a new master or site password. */
    public String checkStrength(String password) {
        if (password.length() < 6) return "WEAK";
        boolean hasUpper = !password.equals(password.toLowerCase());
        boolean hasDigit = password.chars().anyMatch(Character::isDigit);
        boolean hasSpecial = password.chars().anyMatch(ch -> !Character.isLetterOrDigit(ch));
        int score = (hasUpper ? 1 : 0) + (hasDigit ? 1 : 0) + (hasSpecial ? 1 : 0);
        if (score >= 3 && password.length() >= 10) return "STRONG";
        if (score >= 2) return "MEDIUM";
        return "WEAK";
    }
}
