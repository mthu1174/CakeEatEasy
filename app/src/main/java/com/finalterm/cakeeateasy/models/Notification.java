package com.finalterm.cakeeateasy.models;

public class Notification {

    // Sử dụng Enum để định nghĩa các loại thông báo một cách rõ ràng và an toàn
    public enum NotifType {
        PROMO,      // Ví dụ: mã giảm giá
        SUCCESS,    // Ví dụ: đơn hàng thành công, đã giao
        ERROR,      // Ví dụ: đơn hàng bị hủy
        ACCOUNT     // Ví dụ: cập nhật tài khoản
    }

    private String title;
    private String subtitle;
    private String time;
    private String date;
    private NotifType type;
    private boolean isRead;

    // Constructor để tạo một đối tượng Notification
    public Notification(String title, String subtitle, String time, String date, NotifType type, boolean isRead) {
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.date = date;
        this.type = type;
        this.isRead = isRead;
    }

    // Các hàm getter để lấy dữ liệu ra
    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public NotifType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }
}