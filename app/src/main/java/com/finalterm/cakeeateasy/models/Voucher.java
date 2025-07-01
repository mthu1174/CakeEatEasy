package com.finalterm.cakeeateasy.models;

public class Voucher {
    private String title;
    private String description;
    private int titleAndIconColorRes;
    private int iconBackgroundColorRes;
    private int iconRes;
    private String voucherCode;
    private int discountAmount;

    // THÊM: Thuộc tính để xác định voucher có dùng được hay không
    private boolean isAvailable = true;

    public Voucher(String title, String description, int titleAndIconColorRes, int iconBackgroundColorRes, int iconRes, String voucherCode, int discountAmount) {
        this.title = title;
        this.description = description;
        this.titleAndIconColorRes = titleAndIconColorRes;
        this.iconBackgroundColorRes = iconBackgroundColorRes;
        this.iconRes = iconRes;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
    }

    // Getters
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTitleAndIconColorRes() { return titleAndIconColorRes; }
    public int getIconBackgroundColorRes() { return iconBackgroundColorRes; }
    public int getIconRes() { return iconRes; }
    public String getVoucherCode() { return voucherCode; }
    public int getDiscountAmount() { return discountAmount; }

    // THÊM: Getter và Setter cho trạng thái
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }
}