package com.example.charcuteria.model;

public class Subscription {
    private Integer id;

    private Integer userId;
    private Integer planId;
    private String status; // ACTIVE, PAUSED, CANCELLED
    private String startedAt;

    public Subscription() {}

    public Subscription(Integer userId, Integer planId, String status) {
        this.userId = userId;
        this.planId = planId;
        this.status = status;
    }

    public Integer getId() { return id; }
    public Integer getUserId() { return userId; }
    public Integer getPlanId() { return planId; }
    public String getStatus() { return status; }
    public String getStartedAt() { return startedAt; }

    public void setId(Integer id)  {this.id = id; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public void setPlanId(Integer planId) { this.planId = planId; }
    public void setStatus(String status) { this.status = status; }
    public void setStartedAt(String startedAt) { this.startedAt = startedAt; }

}
