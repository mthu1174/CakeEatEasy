// app/src/main/java/com/finalterm/cakeeateasy/models/Order.java
package com.finalterm.cakeeateasy.models;

public class Order {

    private String orderId; // Lưu mã dạng String như "ABC12"
    private String date;
    private String firstProductName;
    private String firstProductImageUrl;
    private String totalPrice;
    private String status;

    public Order(String orderId, String date, String firstProductName, String firstProductImageUrl, String totalPrice, String status) {
        this.orderId = orderId;
        this.date = date;
        this.firstProductName = firstProductName;
        this.firstProductImageUrl = firstProductImageUrl;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    // Getters
    public String getOrderId() {
        return orderId;
    }

    // === ĐÃ SỬA TÊN HÀM ĐỂ KHỚP VỚI ADAPTER CỦA BẠN ===
    // Hàm này sẽ trả về chuỗi "#ABC12" để hiển thị trên UI
    public String getOrderIdDisplay() {
        return "#" + orderId;
    }

    public String getDate() {
        return date;
    }

    public String getFirstProductName() {
        return firstProductName;
    }

    public String getFirstProductImageUrl() {
        return firstProductImageUrl;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }
}