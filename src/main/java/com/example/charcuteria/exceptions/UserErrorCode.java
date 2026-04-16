package com.example.charcuteria.exceptions;

public enum UserErrorCode {

    EMAIL_ALREADY_EXISTS(409, "Email already exists"),
    USER_NOT_FOUND(404, "User not found"),
    INVALID_PASSWORD(400, "Invalid credentials"),
    DIFFERENT_PASSWORDS(403, "Different passwords");

    private final int status;
    private final String message;

    UserErrorCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
