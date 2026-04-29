package com.example.charcuteria.dto.subscription;

public class SubscriptionPlanResponse {
    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Boolean isActive;

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Boolean getIsActive() { return isActive; }

    public void setId(Integer id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
