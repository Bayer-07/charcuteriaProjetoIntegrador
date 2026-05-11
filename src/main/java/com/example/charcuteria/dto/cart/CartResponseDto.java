package com.example.charcuteria.dto.cart;

import java.math.BigDecimal;

public record CartResponseDto(
    Integer id,
    Integer productId,
    String productName,
    String productFile,
    BigDecimal unitPrice,
    Integer quantity,
    BigDecimal subtotal
) {
    public CartResponseDto(Integer id, Integer productId, String productName, String productFile, BigDecimal unitPrice, Integer quantity) {
        this(id, productId, productName, productFile, unitPrice, quantity,
             unitPrice.multiply(BigDecimal.valueOf(quantity)));
    }
}
