package com.finalterm.cakeeateasy.models;

public class Notification {

    private int notificationId;
    private int customerId;
    private String message;
    private String createdAt;
    private boolean isRead;
    private String orderId;

    public Notification(int notificationId, int customerId, String message, String createdAt, boolean isRead, String orderId) {
        this.notificationId = notificationId;
        this.customerId = customerId;
        this.message = message;
        this.createdAt = createdAt;
        this.isRead = isRead;
        this.orderId = orderId;
    }

    // Getters
    public int getNotificationId() {
        return notificationId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getMessage() {
        return message;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean isRead() {
        return isRead;
    }

    public String getOrderId() {
        return orderId;
    }

    // SỬA LỖI 6: Thêm hàm setter cho isRead
    public void setRead(boolean read) {
        isRead = read;
    }

    // Các hàm tiện ích (giữ nguyên)
    public String getTitle() {
        if (message.toLowerCase().contains("order")) return "Cập nhật đơn hàng";
        if (message.toLowerCase().contains("discount") || message.toLowerCase().contains("promo")) return "Khuyến mãi";
        return "Thông báo";
    }

    public String getSubtitle() {
        return message;
    }

    public NotifType getType() {
        String lowerCaseMessage = message.toLowerCase();
        if (lowerCaseMessage.contains("delivered") || lowerCaseMessage.contains("successful")) return NotifType.SUCCESS;
        if (lowerCaseMessage.contains("canceled")) return NotifType.ERROR;
        if (lowerCaseMessage.contains("discount") || lowerCaseMessage.contains("promo")) return NotifType.PROMO;
        return NotifType.ACCOUNT;
    }

    public String getDate() {
        if (createdAt == null || createdAt.length() < 10) return "";
        try {
            String datePart = createdAt.substring(0, 10);
            String[] parts = datePart.split("-");
            return parts[2] + "/" + parts[1] + "/" + parts[0];
        } catch (Exception e) {
            return "";
        }
    }

    public String getTime() {
        if (createdAt == null || createdAt.length() < 16) return "";
        try {
            return createdAt.substring(11, 16);
        } catch (Exception e) {
            return "";
        }
    }

    public enum NotifType {
        PROMO, SUCCESS, ERROR, ACCOUNT
    }
}