package com.finalterm.cakeeateasy.models;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();
    private int voucherAmount = 0;
    public String deliveryTime = "18:00";
    public String voucherCode = "";
    public String voucherType = "";

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public int getCartCount() {
        int totalCount = 0;
        for (CartItem item : cartItems) {
            totalCount += item.getQuantity();
        }
        return totalCount;
    }

    public void addToCart(CartItem item) {
        for (CartItem ci : cartItems) {
            if (ci.getProductId() == item.getProductId()) {
                ci.setQuantity(ci.getQuantity() + item.getQuantity());
                return;
            }
        }
        cartItems.add(item);
    }

    public void removeFromCart(int productId) {
        cartItems.removeIf(item -> item.getProductId() == productId);
    }

    public void removeItems(List<CartItem> itemsToRemove) {
        for (CartItem itemToRemove : itemsToRemove) {
            removeFromCart(itemToRemove.getProductId());
        }
    }

    public void setVoucherAmount(int amount) {
        voucherAmount = amount;
    }

    public int getVoucherAmount() {
        return voucherAmount;
    }

    public void setDeliveryTime(String time) {
        deliveryTime = time;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setVoucherCode(String code) {
        voucherCode = code;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherType(String type) {
        voucherType = type;
    }

    public String getVoucherType() {
        return voucherType;
    }

    public void clearCart() {
        cartItems.clear();
        voucherAmount = 0;
        voucherCode = "";
        voucherType = "";
        deliveryTime = "18:00";
    }

    // Method to create a temporary cart with only selected items
    public void setTemporaryCart(List<CartItem> selectedItems) {
        cartItems.clear();
        cartItems.addAll(selectedItems);
    }

    // Method to restore the original cart (if needed)
    public void restoreOriginalCart(List<CartItem> originalItems) {
        cartItems.clear();
        cartItems.addAll(originalItems);
    }
} 