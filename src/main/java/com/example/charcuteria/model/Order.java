package com.example.charcuteria.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Order {
    private Integer id;
    private Integer userId;
    private Integer addressId;
    private BigDecimal totalAmount;
    private BigDecimal shippingCost;
    private String status;
    private LocalDateTime orderDate;

    public Order() {}

    public Order(Integer userId, Integer addressId, BigDecimal totalAmount, BigDecimal shippingCost, String status) {
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.status = status;
    }

    public Order(Integer id, Integer userId, Integer addressId, BigDecimal totalAmount, BigDecimal shippingCost, String status, LocalDateTime orderDate) {
        this.id = id;
        this.userId = userId;
        this.addressId = addressId;
        this.totalAmount = totalAmount;
        this.shippingCost = shippingCost;
        this.status = status;
        this.orderDate = orderDate;
    }

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getAddressId() { return addressId; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public String getStatus() { return status; }
    public LocalDateTime getOrderDate() { return orderDate; }

    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
    public void setStatus(String status) { this.status = status; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
}
