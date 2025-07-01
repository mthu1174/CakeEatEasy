package com.finalterm.cakeeateasy.models;

import java.io.Serializable;

public class CartItem implements Serializable {
    // Thuộc tính từ bảng OrderItem
    private int orderItemId;
    private int quantity;

    // Thuộc tính từ bảng Product (JOIN vào)
    private int productId;
    private String name;
    private double price; // Giá tại thời điểm thêm vào giỏ
    private String imageUrl;

    // Thuộc tính chỉ dùng cho UI
    private boolean selected;

    /**
     * Constructor dùng để DatabaseHelper tạo đối tượng từ DB.
     */
    public CartItem(int orderItemId, int productId, String name, double price, String imageUrl, int quantity) {
        this.orderItemId = orderItemId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
        this.selected = true; // Mặc định là được chọn khi hiển thị
    }

    // --- Getters and Setters ---
    public int getOrderItemId() { return orderItemId; }
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
}