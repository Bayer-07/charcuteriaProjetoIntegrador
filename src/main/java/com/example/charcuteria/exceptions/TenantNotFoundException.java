package com.example.charcuteria.exceptions;

public class TenantNotFoundException extends RuntimeException {
    public TenantNotFoundException(String message) {
        super(message);
    }
}