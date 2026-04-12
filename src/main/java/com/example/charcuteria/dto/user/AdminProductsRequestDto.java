package com.example.charcuteria.dto.user;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class AdminProductsRequestDto {
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private MultipartFile image;

    public AdminProductsRequestDto() {}

    public AdminProductsRequestDto(String name, String description, String category, BigDecimal price, Integer stock, MultipartFile image) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.image = image;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public MultipartFile getImage() { return image; }
    public void setImage(MultipartFile image) { this.image = image; }
}
