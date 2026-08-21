-- Secure Password Manager - Database Schema
-- Run this once before starting the app:  mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS password_manager_db;
USE password_manager_db;

-- Application users (the people who log in to the manager)
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,   -- SHA-256 hash of the master password, never plaintext
    salt          VARCHAR(64)  NOT NULL,   -- unique per-user salt used in hashing
    role          ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Stored credentials, one row per saved login. site_password is encrypted (AES),
-- not hashed, because the app needs to be able to decrypt and show it back to the user.
CREATE TABLE IF NOT EXISTS credentials (
    credential_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id       INT NOT NULL,
    site_name     VARCHAR(100) NOT NULL,
    site_username VARCHAR(100) NOT NULL,
    site_password VARCHAR(500) NOT NULL,   -- AES-encrypted, Base64-encoded
    notes         VARCHAR(255),
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_credentials_user
        FOREIGN KEY (user_id) REFERENCES users(user_id)
        ON DELETE CASCADE
);

CREATE INDEX idx_credentials_user ON credentials(user_id);
CREATE INDEX idx_credentials_site ON credentials(site_name);
