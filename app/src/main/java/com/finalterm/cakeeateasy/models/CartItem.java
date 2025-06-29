package com.finalterm.cakeeateasy.models;

public class CartItem {
    private int productId;
    private String name;
    private String price;
    private String discount;
    private int imageResId;
    private int quantity;
    private boolean selected;

    public CartItem(int productId, String name, String price, String discount, int imageResId, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.discount = discount;
        this.imageResId = imageResId;
        this.quantity = quantity;
        this.selected = true;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getDiscount() { return discount; }
    public void setDiscount(String discount) { this.discount = discount; }
    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public boolean isSelected() { return selected; }
    public void setSelected(boolean selected) { this.selected = selected; }
} 