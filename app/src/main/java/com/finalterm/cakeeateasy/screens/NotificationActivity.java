package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.NotificationAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Notification;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

// SỬA LỖI 1 & 4: implement listener đã được định nghĩa đúng
public class NotificationActivity extends BaseActivity implements NotificationAdapter.OnNotificationClickListener {

    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private MaterialToolbar toolbar;
    private TextView txtEmptyMessage;

    private DatabaseHelper dbHelper;
    private List<Notification> notificationList;
    private int currentUserId = -1;

    @Override
    public int getNavItemIndex() {
        return 3;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_notification);

        dbHelper = new DatabaseHelper(this);

        loadCurrentUserId();
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem thông báo.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotificationsFromDb();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvNotifications = findViewById(R.id.rv_notifications);
        // SỬA LỖI 2: Ánh xạ view đã được thêm vào XML
        txtEmptyMessage = findViewById(R.id.txt_empty_message);

        //toolbar.setTitle("Thông báo"); // MaterialToolbar đã có title trong XML
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    private void setupRecyclerView() {
        rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        notificationList = new ArrayList<>();
        // SỬA LỖI 3: Gọi constructor đã được sửa trong Adapter
        adapter = new NotificationAdapter(this, notificationList, this);
        rvNotifications.setAdapter(adapter);
    }

    private void loadNotificationsFromDb() {
        if (currentUserId == -1) return;

        List<Notification> notificationsFromDb = dbHelper.getNotificationsByCustomerId(currentUserId);

        this.notificationList.clear();
        if (notificationsFromDb != null) {
            this.notificationList.addAll(notificationsFromDb);
        }
        adapter.notifyDataSetChanged();

        checkEmptyState();
    }

    private void checkEmptyState() {
        if (notificationList.isEmpty()) {
            rvNotifications.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            rvNotifications.setVisibility(View.VISIBLE);
            txtEmptyMessage.setVisibility(View.GONE);
        }
    }

    // SỬA LỖI 4: Bây giờ @Override là hoàn toàn chính xác
    @Override
    public void onNotificationClick(Notification notification, int position) {
        if (!notification.isRead()) {
            // SỬA LỖI 5: Dùng getNotificationId() thay vì getId()
            boolean isMarked = dbHelper.markNotificationAsRead(notification.getNotificationId());

            if (isMarked) {
                // SỬA LỖI 6: Dùng setRead() đã được thêm vào Model
                notification.setRead(true);
                adapter.notifyItemChanged(position);
            }
        }

        Toast.makeText(this, "Đã xem: " + notification.getTitle(), Toast.LENGTH_SHORT).show();
        // TODO: Điều hướng nếu cần
    }
}