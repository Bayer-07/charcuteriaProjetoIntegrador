package com.example.charcuteria.dto.user;

public class AdminProductsResponseDto {
    private final int id;
    private final String category;
    private final String name;
    private final double price;
    private final Boolean isActive;

    public AdminProductsResponseDto(int id, String category, String name, double price, Boolean isActive) {
        this.id = id;
        this.category = category;
        this.name = name;
        this.price = price;
        this.isActive = isActive;
    }

    public int getId() {return id;}
    public String getCategory() {return category;}
    public String getName() {return name;}
    public double getPrice() {return price;}
    public Boolean getIsActive() {return isActive;}
}
