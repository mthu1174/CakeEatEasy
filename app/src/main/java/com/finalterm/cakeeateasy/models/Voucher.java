package com.finalterm.cakeeateasy.models;

public class Voucher {
    private String title;
    private String description;
    private int titleAndIconColorRes; // Màu cho chữ và icon (ví dụ: R.color.voucher_orange)
    private int iconBackgroundColorRes; // Màu cho nền tròn (ví dụ: R.color.voucher_icon_bg_orange)
    private int iconRes;
    private String voucherCode;
    private int discountAmount;

    public Voucher(String title, String description, int titleAndIconColorRes, int iconBackgroundColorRes, int iconRes, String voucherCode, int discountAmount) {
        this.title = title;
        this.description = description;
        this.titleAndIconColorRes = titleAndIconColorRes;
        this.iconBackgroundColorRes = iconBackgroundColorRes;
        this.iconRes = iconRes;
        this.voucherCode = voucherCode;
        this.discountAmount = discountAmount;
    }

    // Thêm các hàm get...() ở đây
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public int getTitleAndIconColorRes() { return titleAndIconColorRes; }
    public int getIconBackgroundColorRes() { return iconBackgroundColorRes; }
    public int getIconRes() { return iconRes; }
    public String getVoucherCode() { return voucherCode; }
    public int getDiscountAmount() { return discountAmount; }
}