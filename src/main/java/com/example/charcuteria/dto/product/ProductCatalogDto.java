package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

public class ProductCatalogDto {
    private final Integer id;
    private final String name;
    private final String description;
    private final String categoryName;
    private final BigDecimal price;
    private final Integer stockQuantity;
    private final String imagePath;

    public ProductCatalogDto(Integer id, String name, String description, String categoryName, BigDecimal price, Integer stockQuantity, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryName = categoryName;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.imagePath = imagePath;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public String getImagePath() {
        return imagePath;
    }
}
