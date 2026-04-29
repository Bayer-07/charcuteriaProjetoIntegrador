package com.example.charcuteria.dto.subscription;

public class SubscriptionRequest {
    private Integer userId;
    private Integer planId;
    private String status;
    private String startedAt;

    public Integer getUserId() { return userId; }
    public Integer getPlanId() { return planId; }
    public String getStatus() { return status; }
    public String getStartedAt() { return startedAt; }

    public void setUserId(Integer userId) { this.userId = userId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public void setStatus(String status) { this.status = status; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }
}
