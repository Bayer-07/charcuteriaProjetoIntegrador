package com.example.charcuteria.dto.product;

import java.math.BigDecimal;

import org.springframework.web.multipart.MultipartFile;

public class ProductsEditRequestDto {
    private Integer id;
    private String name;
    private String description;
    private String category;
    private BigDecimal price;
    private Integer stock;
    private MultipartFile file;

    public ProductsEditRequestDto() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

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

    public MultipartFile getFile() { return file; }
    public void setFile(MultipartFile file) { this.file = file; }
}
