package com.example.charcuteria.dto.user;

import java.math.BigDecimal;

public class AdminProductsRequestDto {
    private String name;
    private String description;
    private String category;
    private BigDecimal price;

    public AdminProductsRequestDto() {}

    public AdminProductsRequestDto(String name, String description, String category, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
