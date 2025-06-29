package com.finalterm.cakeeateasy.models;

public class Product {
    private String name, description, price, oldPrice, discount;
    private int imageRes;
    public Product(String name, String description, String price, String oldPrice, String discount, int imageRes) { this.name = name; this.description = description; this.price = price; this.oldPrice = oldPrice; this.discount = discount; this.imageRes = imageRes; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public String getOldPrice() { return oldPrice; }
    public String getDiscount() { return discount; }
    public int getImageRes() { return imageRes; }
}
