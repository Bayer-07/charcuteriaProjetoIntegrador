package com.example.charcuteria.dto.subscription;

public class UserSubscriptionResponseDto {
    private Integer subscriptionId;
    private String planName;
    private String planDescription;
    private Double planPrice;
    private String status;
    private String startedAt;

    public UserSubscriptionResponseDto() {}

    public UserSubscriptionResponseDto(Integer subscriptionId, String planName, String planDescription, Double planPrice, String status, String startedAt) {
        this.subscriptionId = subscriptionId;
        this.planName = planName;
        this.planDescription = planDescription;
        this.planPrice = planPrice;
        this.status = status;
        this.startedAt = startedAt;
    }

    public Integer getSubscriptionId() { return subscriptionId; }
    public String getPlanName() { return planName; }
    public String getPlanDescription() { return planDescription; }
    public Double getPlanPrice() { return planPrice; }
    public String getStatus() { return status; }
    public String getStartedAt() { return startedAt; }

    public void setSubscriptionId(Integer subscriptionId) { this.subscriptionId = subscriptionId; }
    public void setPlanName(String planName) { this.planName = planName; }
    public void setPlanDescription(String planDescription) { this.planDescription = planDescription; }
    public void setPlanPrice(Double planPrice) { this.planPrice = planPrice; }
    public void setStatus(String status) { this.status = status; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }
}
