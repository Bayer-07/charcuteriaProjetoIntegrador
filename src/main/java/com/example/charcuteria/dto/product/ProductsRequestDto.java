package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

import org.springframework.format.annotation.NumberFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Min;

public class ProductsRequestDto {
    private String name;
    private String description;
    private String category;

    @NumberFormat(pattern = "#,##0.00")
    private BigDecimal price;

    @Min(value = 0, message = "A quantidade não pode ser negativa")
    private Integer stock;
    
    private MultipartFile image;

    public ProductsRequestDto() {}

    public ProductsRequestDto(String name, String description, String category, BigDecimal price, Integer stock, MultipartFile image) {
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
