package com.yashi.passwordmanager.util;

/**
 * Application-level checked exception. Wrapping raw SQLExceptions in this
 * lets the UI layer show a clean, human-readable message instead of a
 * stack trace, while the original cause is still preserved for debugging.
 */
public class AppException extends Exception {
    public AppException(String message) {
        super(message);
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}
