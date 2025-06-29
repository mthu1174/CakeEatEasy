package com.finalterm.cakeeateasy.models;

public class SelectableVoucher {
    private String title;
    private String validity;
    private int iconRes;
    private boolean isAvailable;
    private boolean isSelected; // Để quản lý trạng thái của RadioButton
    private String voucherCode;
    private int discountAmount;

    // Constructor, Getters và Setters...
    public SelectableVoucher(String title, String validity, int iconRes, boolean isAvailable, String voucherCode, int discountAmount) {
        this.title = title;
        this.validity = validity;
        this.iconRes = iconRes;
        this.isAvailable = isAvailable;
        this.isSelected = false; // Mặc định không được chọn
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }
    // ... thêm các hàm get/set ...
}