package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

public class TopProductResponseDto {
    private final int id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final String imagePath;
    private final int totalPurchased;

    public TopProductResponseDto(int id, String name, String description, BigDecimal price, String imagePath, int totalPurchased) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imagePath = imagePath;
        this.totalPurchased = totalPurchased;
    }

    public int getId() {return id;}
    public String getName() {return name;}
    public String getDescription() {return description;}
    public BigDecimal getPrice() {return price;}
    public String getImagePath() {return imagePath;}
    public int getTotalPurchased() {return totalPurchased;}
}
