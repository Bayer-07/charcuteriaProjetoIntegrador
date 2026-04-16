package com.example.charcuteria.exceptions;

public class BusinessException extends RuntimeException {
    private UserErrorCode userErrorCode;
    private ProductErrorCode productErrorCode;

    public BusinessException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
        this.userErrorCode = userErrorCode;
    }

    public BusinessException(ProductErrorCode productErrorCode) {
        super(productErrorCode.getMessage());
        this.productErrorCode = productErrorCode;
    }

    public UserErrorCode getUserErrorCode() {
        return userErrorCode;
    }

    public ProductErrorCode getProductErrorCode() {
        return productErrorCode;
    }
}
