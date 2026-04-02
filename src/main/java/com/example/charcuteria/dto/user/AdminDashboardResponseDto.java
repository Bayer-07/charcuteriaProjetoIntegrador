package com.example.charcuteria.dto.user;

public class AdminDashboardResponseDto {
    private final int count;
    private final double price;
    private final int subscriptions;
    private final int storage;

    public AdminDashboardResponseDto(int count, double price, int subscriptions, int storage) {
        this.count = count;
        this.price = price;
        this.subscriptions = subscriptions;
        this.storage = storage;
    }

    public int getCount() { return count; }
    public double getPrice() { return price; }
    public int getSubscriptions() { return subscriptions; }
    public int getStorage() { return storage; }
}
