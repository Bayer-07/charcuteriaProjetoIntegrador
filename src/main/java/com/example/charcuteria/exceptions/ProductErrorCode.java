package com.example.charcuteria.exceptions;

public enum ProductErrorCode {

    PRODUCT_NOT_FOUND(404, "Product not found, cannot delete it");

    private final int status;
    private final String message;

    ProductErrorCode(int status, String message) {
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
