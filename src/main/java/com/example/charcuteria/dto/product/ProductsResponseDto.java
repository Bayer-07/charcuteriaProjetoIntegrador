package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

import org.springframework.format.annotation.NumberFormat;

public class ProductsResponseDto {
    private final int id;
    private final int stockQuantity;
    private final String category;
    private final String name;

    @NumberFormat(pattern = "#,##0.00")
    private final BigDecimal price;

    public ProductsResponseDto(int id, int stockQuantity, String category, String name, BigDecimal price) {
        this.id = id;
        this.stockQuantity = stockQuantity;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public int getId() {return id;}
    public int getStockQuantity() {return stockQuantity;}
    public String getCategory() {return category;}
    public String getName() {return name;}
    public BigDecimal getPrice() {return price;}
}
