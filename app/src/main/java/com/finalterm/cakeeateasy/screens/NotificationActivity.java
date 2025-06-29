package com.finalterm.cakeeateasy.screens;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.NotificationAdapter;
import com.finalterm.cakeeateasy.models.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationActivity extends com.finalterm.cakeeateasy.screens.BaseActivity {

    RecyclerView rvNotifications;
    NotificationAdapter adapter;
    List<Notification> notificationList;

    @Override
    public int getNavItemIndex() {
        // Vị trí của tab Notification trên thanh điều hướng
        return 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_notification);

        rvNotifications = findViewById(R.id.rv_notifications);
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        // Tải dữ liệu và thiết lập adapter
        loadNotifications();
        adapter = new NotificationAdapter(this, notificationList);
        rvNotifications.setAdapter(adapter);

        // Xử lý nút back trên toolbar
        findViewById(R.id.toolbar_notification).setOnClickListener(v -> onBackPressed());
    }

    // Trong NotificationActivity.java
    private void loadNotifications() {
        notificationList = new ArrayList<>();
        // Giờ đây bạn có thể tạo thông báo với các loại khác nhau
        notificationList.add(new Notification("Get 5% Discount Code", "Discount code for new users.", "11:10", "10/05/2024", Notification.NotifType.PROMO, false));
        notificationList.add(new Notification("Order Received", "Order #SP_0023900 has been delivered.", "11:10", "10/05/2024", Notification.NotifType.SUCCESS, true));
        notificationList.add(new Notification("Order Cancelled", "Order #SP_0023450 has been cancelled.", "22:40", "09/05/2024", Notification.NotifType.ERROR, true));
        notificationList.add(new Notification("Account Setup Successful", "Congratulations!", "20:16", "08/06/2024", Notification.NotifType.ACCOUNT, true));
    }
}