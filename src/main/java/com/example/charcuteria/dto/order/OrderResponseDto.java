package com.example.charcuteria.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Year;

public class OrderResponseDto {
    private Integer id;
    private Integer year;
    private LocalDateTime orderDate;
    private String status;
    private BigDecimal totalAmount;

    public OrderResponseDto() {}

    public OrderResponseDto(Integer id, LocalDateTime orderDate, String status, BigDecimal totalAmount) {
        this.id = id;
        this.orderDate = orderDate;
        this.year = Year.now().getValue();
        this.status = status;
        this.totalAmount = totalAmount;
    }

    public Integer getId() { return id; }
    public Integer getYear() { return year; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public BigDecimal getTotalAmount() { return totalAmount; }

    public void setId(Integer id) { this.id = id; }
    public void setYear(Integer year) { this.year = year; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
}
