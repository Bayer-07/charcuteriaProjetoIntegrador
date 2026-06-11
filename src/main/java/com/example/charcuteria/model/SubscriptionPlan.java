package com.example.charcuteria.model;

public class SubscriptionPlan {
    private Integer id;

    private String name;
    private String description;
    private Double price;
    private Boolean isActive;

    public SubscriptionPlan() {}

    public SubscriptionPlan(Integer id, String name, String description, Double price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.isActive = true;
    }

    public Integer getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Double getPrice() { return price; }
    public Boolean getIsActive() { return isActive; }

    public void setId(Integer id) {this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(Double price) { this.price = price; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

}
