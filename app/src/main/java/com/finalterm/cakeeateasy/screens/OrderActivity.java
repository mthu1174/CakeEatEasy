// trong package com.finalterm.cakeeateasy.screens
package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.OrderHistoryAdapter;
import com.finalterm.cakeeateasy.models.Order;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TabLayout tabLayout;
    private RecyclerView rvOrders;
    private OrderHistoryAdapter adapter;
    private List<Order> allOrders; // Danh sách chứa TẤT CẢ đơn hàng

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        initViews();
        setupToolbar();
        loadDummyData();
        setupRecyclerView();
        setupTabs();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tabLayout = findViewById(R.id.tab_layout); // ID từ component_order_tab.xml
        rvOrders = findViewById(R.id.rv_orders);
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadDummyData() {
        allOrders = new ArrayList<>();
        // Dữ liệu cho tab "Ongoing"
        allOrders.add(new Order("#9ds69hs", "06/08/2025", "Jolie", R.drawable.placeholder_cake_promo, "1.125.000 đ", "Ongoing"));
        allOrders.add(new Order("#5ds69hw", "06/08/2025", "Sunshine", R.drawable.placeholder_cake_promo, "650.000 đ", "Ongoing"));

        // Dữ liệu cho tab "History"
        allOrders.add(new Order("#9ds69hs", "05/08/2025", "Jolie", R.drawable.placeholder_cake_promo, "950.000 đ", "Completed"));
        allOrders.add(new Order("#9ds69hs", "04/08/2025", "Orchid Divine", R.drawable.placeholder_cake_promo, "950.000 đ", "Completed"));
        allOrders.add(new Order("#abc1234", "03/08/2025", "Ever Muse", R.drawable.placeholder_cake_promo, "950.000 đ", "Cancelled"));
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(this));
        // Khởi tạo adapter với danh sách trống ban đầu
        adapter = new OrderHistoryAdapter(this, new ArrayList<>());
        rvOrders.setAdapter(adapter);
    }

    private void setupTabs() {
        // Không cần thêm tab item trong Java vì đã có trong XML
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                filterOrders(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Hiển thị danh sách cho tab đầu tiên (Ongoing) khi mới mở màn hình
        filterOrders(0);
    }

    private void filterOrders(int tabPosition) {
        List<Order> filteredList;
        if (tabPosition == 0) { // Tab "Ongoing"
            filteredList = allOrders.stream()
                    .filter(order -> "Ongoing".equalsIgnoreCase(order.getStatus()))
                    .collect(Collectors.toList());
        } else { // Tab "History"
            filteredList = allOrders.stream()
                    .filter(order -> !"Ongoing".equalsIgnoreCase(order.getStatus()))
                    .collect(Collectors.toList());
        }
        // Cập nhật dữ liệu cho adapter
        adapter.updateOrders(filteredList);
    }
}