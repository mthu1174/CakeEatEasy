package com.finalterm.cakeeateasy.models;

public class Order {
    private String id;
    private String date;
    private String productName;
    private int productImageRes;
    private String price;
    private String status; // "Ongoing", "Completed", "Cancelled"

    public Order(String id, String date, String productName, int productImageRes, String price, String status) {
        this.id = id;
        this.date = date;
        this.productName = productName;
        this.productImageRes = productImageRes;
        this.price = price;
        this.status = status;
    }

    // Getters cho tất cả các thuộc tính
    public String getId() { return id; }
    public String getDate() { return date; }
    public String getProductName() { return productName; }
    public int getProductImageRes() { return productImageRes; }
    public String getPrice() { return price; }
    public String getStatus() { return status; }
}