package com.finalterm.cakeeateasy.models;

public class OrderItem {
    private int orderItemId;
    private int quantity;
    private double price; // Giá tại thời điểm mua
    private int productId;
    private Integer cartId; // Có thể null
    private Integer orderId; // Có thể null

    /**
     * Constructor dùng để tạo mới.
     */
    public OrderItem(int quantity, double price, int productId, Integer cartId, Integer orderId) {
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
        this.cartId = cartId;
        this.orderId = orderId;
    }

    /**
     * Constructor dùng để đọc từ DB.
     */
    public OrderItem(int orderItemId, int quantity, double price, int productId, Integer cartId, Integer orderId) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.price = price;
        this.productId = productId;
        this.cartId = cartId;
        this.orderId = orderId;
    }

    // --- Getters and Setters ---
    public int getOrderItemId() { return orderItemId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getPrice() { return price; }
    public int getProductId() { return productId; }
    public Integer getCartId() { return cartId; }
    public void setCartId(Integer cartId) { this.cartId = cartId; }
    public Integer getOrderId() { return orderId; }
    public void setOrderId(Integer orderId) { this.orderId = orderId; }
}