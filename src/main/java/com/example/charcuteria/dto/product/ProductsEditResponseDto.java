package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

public class ProductsEditResponseDto {
    private final String name;
    private final String description;
    private final String category;
    private final BigDecimal price;
    private final Integer stock;
    private final String file;

    public ProductsEditResponseDto(String name, String desciption, String category, BigDecimal price, Integer stock, String file) {
        this.name = name;
        this.description = desciption;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.file = file;
    }

    public String getName() {return name;}
    public String getDescription() {return description;}
    public String getCategory() {return category;}
    public BigDecimal getPrice() {return price;}
    public Integer getStock() {return stock;}
    public String getFile() {return file;}

}
