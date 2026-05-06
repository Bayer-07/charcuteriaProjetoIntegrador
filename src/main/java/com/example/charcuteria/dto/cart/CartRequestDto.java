package com.example.charcuteria.dto.cart;

public class CartRequestDto {
    private final Integer productId;

    public CartRequestDto(Integer productId) {
        this.productId = productId;
    }

    public Integer getProductid() {
        return productId;
    }

}
