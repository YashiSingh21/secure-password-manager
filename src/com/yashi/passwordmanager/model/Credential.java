package com.yashi.passwordmanager.model;

import java.sql.Timestamp;

/**
 * Represents one saved login (a "vault entry") belonging to a user.
 * sitePassword is kept ENCRYPTED here - it is only decrypted right before
 * being shown on screen, never stored in plaintext.
 */
public class Credential {
    private int credentialId;
    private int userId;
    private String siteName;
    private String siteUsername;
    private String sitePassword; // encrypted form
    private String notes;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Credential() {
    }

    public Credential(int credentialId, int userId, String siteName, String siteUsername,
                       String sitePassword, String notes) {
        this.credentialId = credentialId;
        this.userId = userId;
        this.siteName = siteName;
        this.siteUsername = siteUsername;
        this.sitePassword = sitePassword;
        this.notes = notes;
    }

    public int getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(int credentialId) {
        this.credentialId = credentialId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteUsername() {
        return siteUsername;
    }

    public void setSiteUsername(String siteUsername) {
        this.siteUsername = siteUsername;
    }

    public String getSitePassword() {
        return sitePassword;
    }

    public void setSitePassword(String sitePassword) {
        this.sitePassword = sitePassword;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
