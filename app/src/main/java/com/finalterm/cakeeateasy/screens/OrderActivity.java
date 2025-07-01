package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.Intent; // Thêm import này
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.OrderAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Order;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

// BƯỚC 1: Implement listener của OrderAdapter
public class OrderActivity extends BaseActivity implements OrderAdapter.OrderItemListener {

    private static final String TAG = "OrderActivity";

    // --- UI Elements ---
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private TextView txtEmptyMessage;
    private MaterialToolbar toolbar;

    // --- Data & Logic ---
    private OrderAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private int currentUserId = - 1;

    @Override
    public int getNavItemIndex() {
        return 1; // Index của Order
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentLayout(R.layout.activity_order);

        View contentView = findViewById(R.id.content_frame);

        dbHelper = new DatabaseHelper(this);
        initViews(contentView);
        setupRecyclerView();
        setupTabs();
        setupToolbar();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        // THAY ĐỔI ID Ở ĐÂY CHO KHỚP VỚI FILE XML component_order_tabs.xml
        tabLayout = view.findViewById(R.id.tab_layout);
        rvOrders = view.findViewById(R.id.rv_orders);
        txtEmptyMessage = view.findViewById(R.id.txt_order_empty);

        if (rvOrders == null || txtEmptyMessage == null || tabLayout == null || toolbar == null) {
            Log.e(TAG, "Lỗi nghiêm trọng: Không thể tìm thấy một hoặc nhiều View trong layout.");
            Toast.makeText(this, "Lỗi giao diện, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        // BƯỚC 2: SỬA LẠI DÒNG NÀY ĐỂ TRUYỀN LISTENER (tham số thứ 3)
        adapter = new OrderAdapter(this, orderList, this);
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        rvOrders.setAdapter(adapter);
    }

    private void setupTabs() {
        // Tải dữ liệu lần đầu
        loadOrdersFromDatabase();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadOrdersFromDatabase();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadOrdersFromDatabase() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", - 1);

        if (this.currentUserId == - 1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem đơn hàng.", Toast.LENGTH_LONG).show();
            if (adapter != null) adapter.updateOrders(new ArrayList<>());
            checkEmptyState();
            return;
        }

        List<String> statusesToLoad = new ArrayList<>();
        int selectedTabPosition = tabLayout.getSelectedTabPosition();

        if (selectedTabPosition == 0) {
            statusesToLoad.add("Ongoing");
        } else {
            statusesToLoad.add("Completed");
            statusesToLoad.add("Cancelled");
        }

        List<Order> loadedOrders = dbHelper.getOrdersByStatus(currentUserId, statusesToLoad);
        if (adapter != null) {
            adapter.updateOrders(loadedOrders);
        }
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (txtEmptyMessage == null || rvOrders == null) {
            return;
        }

        if (adapter == null || adapter.getItemCount() == 0) {
            rvOrders.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            txtEmptyMessage.setVisibility(View.GONE);
        }
    }

    // BƯỚC 3: THÊM CÁC PHƯƠNG THỨC BẮT BUỘC CỦA LISTENER
    @Override
    public void onOrderClicked(Order order) {
        Toast.makeText(this, "Mở chi tiết đơn hàng: " + order.getOrderId(), Toast.LENGTH_SHORT).show();
        // Ví dụ:
        // Intent intent = new Intent(this, OrderDetailsActivity.class);
        // intent.putExtra("ORDER_ID", order.getOrderId());
        // startActivity(intent);
    }

    @Override
    public void onReorderClicked(Order order) {
        Toast.makeText(this, "Tính năng đặt lại đơn hàng " + order.getOrderId() + " sắp ra mắt!", Toast.LENGTH_SHORT).show();
        // Logic đặt lại đơn hàng sẽ ở đây
    }

    @Override
    public void onReviewClicked(Order order) {
        // Dòng log này để bạn biết sự kiện đã được kích hoạt
        Log.d("OrderActivity", "Nút Review được nhấn! Chuẩn bị mở ReviewActivity.");

        // 1. Tạo Intent (ý định) để chuyển từ màn hình này (OrderActivity)
        //    đến màn hình đích (ReviewActivity)
        Intent intent = new Intent(OrderActivity.this, ReviewActivity.class);

        // 2. Thực hiện việc chuyển màn hình
        startActivity(intent);
}
}