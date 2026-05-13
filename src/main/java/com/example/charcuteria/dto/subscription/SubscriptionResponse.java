package com.example.charcuteria.dto.subscription;

public class SubscriptionResponse {
    private Integer id;
    private Integer userId;
    private Integer planId;
    private String planName;
    private String status;
    private String startedAt;
    private Double price;
    private String userName;
    private String userEmail;

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getPlanId() { return planId; }
    public String getPlanName() { return planName; }
    public String getStatus() { return status; }
    public String getStartedAt() { return startedAt; }
    public Double getPrice() { return price; }
    public String getUserName() {return userName; }
    public String getUserEmail() {return userEmail; }

    public void setId(Integer id) { this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public void setPlanName(String planName) { this.planName = planName; }
    public void setStatus(String status) { this.status = status; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }
    public void setPrice(Double price) { this.price = price; }
    public void setUserName(String userName) {this.userName = userName; }
    public void setUserEmail(String userEmail) {this.userEmail = userEmail; }
}
