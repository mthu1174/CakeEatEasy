package com.finalterm.cakeeateasy.models;

public class CustomerOrder {
    private int orderId;
    private int customerId;
    private String orderDate;
    private double total;
    private String status;

    /**
     * Constructor dùng để tạo mới.
     */
    public CustomerOrder(int customerId, double total, String status) {
        this.customerId = customerId;
        this.total = total;
        this.status = status;
    }

    /**
     * Constructor dùng để đọc từ DB.
     */
    public CustomerOrder(int orderId, int customerId, String orderDate, double total, String status) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.total = total;
        this.status = status;
    }

    // --- Getters ---
    public int getOrderId() { return orderId; }
    public int getCustomerId() { return customerId; }
    public String getOrderDate() { return orderDate; }
    public double getTotal() { return total; }
    public String getStatus() { return status; }
}