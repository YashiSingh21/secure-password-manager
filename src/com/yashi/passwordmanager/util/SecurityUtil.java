package com.yashi.passwordmanager.util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Handles all cryptography for the app:
 *  - Master login passwords are one-way HASHED (SHA-256 + per-user salt) so even
 *    the app itself can never recover the original master password.
 *  - Saved site passwords are two-way ENCRYPTED (AES) because the user needs to
 *    be able to view/copy them again later.
 *
 * NOTE: For a college/portfolio project this is a solid, explainable design.
 * A production system would use bcrypt/Argon2 for hashing and a proper key
 * management service (KMS) instead of a hardcoded AES key.
 */
public class SecurityUtil {

    // In a real product this key would come from a KMS / environment variable,
    // never hardcoded. Kept simple here so the project is self-contained and
    // easy to explain in an interview.
    private static final String AES_KEY = "MySecretKey12345"; // 16 chars = AES-128
    private static final String AES_ALGO = "AES/CBC/PKCS5Padding";

    private SecurityUtil() {
    }

    // ---------- Master password hashing (one-way) ----------

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    public static String hashPassword(String plainPassword, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(Base64.getDecoder().decode(salt));
            byte[] hashedBytes = digest.digest(plainPassword.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(hashedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String plainPassword, String salt, String expectedHash) {
        String actualHash = hashPassword(plainPassword, salt);
        return actualHash.equals(expectedHash);
    }

    // ---------- Site password encryption (two-way, AES) ----------

    public static String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(AES_KEY.getBytes("UTF-8"));
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            throw new RuntimeException("Error encrypting password", e);
        }
    }

    public static String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance(AES_ALGO);
            SecretKeySpec keySpec = new SecretKeySpec(AES_KEY.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(AES_KEY.getBytes("UTF-8"));
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decoded = Base64.getDecoder().decode(encryptedText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("Error decrypting password", e);
        }
    }
}
