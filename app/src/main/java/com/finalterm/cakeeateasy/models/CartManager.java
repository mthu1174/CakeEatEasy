package com.finalterm.cakeeateasy.models;

import java.util.ArrayList;
import java.util.List;

public class CartManager {
    private static CartManager instance;
    private final List<CartItem> cartItems = new ArrayList<>();
    private int voucherAmount = 0;

    private CartManager() {}

    public static CartManager getInstance() {
        if (instance == null) instance = new CartManager();
        return instance;
    }

    public List<CartItem> getCartItems() {
        return cartItems;
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

    public void setVoucherAmount(int amount) {
        voucherAmount = amount;
    }

    public int getVoucherAmount() {
        return voucherAmount;
    }

    public void clearCart() {
        cartItems.clear();
        voucherAmount = 0;
    }
} 