package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    private List<View> navItems;

    public abstract int getNavItemIndex();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        // Đoạn code này để xử lý padding khi dùng EdgeToEdge
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.base_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        // Chỉ tìm view và gán sự kiện click một lần trong onCreate
        setupNavItems();
        setupNavClickListeners();
    }

    // === THAY ĐỔI QUAN TRỌNG: Thêm onResume() ===
    @Override
    protected void onResume() {
        super.onResume();
        // Cập nhật trạng thái UI mỗi khi Activity được hiển thị lại
        updateNavState();
    }

    protected void setContentLayout(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        getLayoutInflater().inflate(layoutResID, contentFrame, true);
    }

    private void setupNavItems() {
        View navContainer = findViewById(R.id.custom_bottom_nav);
        navItems = Arrays.asList(
                navContainer.findViewById(R.id.nav_home),
                navContainer.findViewById(R.id.nav_order),
                navContainer.findViewById(R.id.nav_favourite),
                navContainer.findViewById(R.id.nav_notification),
                navContainer.findViewById(R.id.nav_profile)
        );
    }

    private void setupNavClickListeners() {
        for (int i = 0; i < navItems.size(); i++) {
            final int index = i;
            View item = navItems.get(i);
            item.setOnClickListener(v -> {
                if (index != getNavItemIndex()) {
                    Class<?> targetActivityClass = getTargetActivity(index);
                    if (targetActivityClass != null) {
                        Intent intent = new Intent(this, targetActivityClass);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }
                }
            });
        }
    }

    // === THAY ĐỔI QUAN TRỌNG: Tách logic cập nhật UI ra hàm riêng ===
    private void updateNavState() {
        if (navItems == null) return;
        for (int i = 0; i < navItems.size(); i++) {
            setItemState(navItems.get(i), i, i == getNavItemIndex());
        }
    }

    private void setItemState(View item, int index, boolean isActive) {
        View activeStateLayout = item.findViewById(R.id.nav_item_active);
        ImageView inactiveIcon = item.findViewById(R.id.nav_item_inactive);
        FloatingActionButton activeFab = item.findViewById(R.id.nav_item_active_fab);
        TextView label = item.findViewById(R.id.nav_item_active_label);

        NavItemInfo info = getNavItemInfo(index);

        activeFab.setImageResource(info.activeIconRes);
        inactiveIcon.setImageResource(info.inactiveIconRes);
        label.setText(info.label);

        if (isActive) {
            activeStateLayout.setVisibility(View.VISIBLE);
            inactiveIcon.setVisibility(View.INVISIBLE);
        } else {
            activeStateLayout.setVisibility(View.INVISIBLE);
            inactiveIcon.setVisibility(View.VISIBLE);
        }
    }

    // Helper class để chứa thông tin item
    private static class NavItemInfo {
        final int inactiveIconRes;
        final int activeIconRes;
        final String label;

        NavItemInfo(int inactiveIconRes, int activeIconRes, String label) {
            this.inactiveIconRes = inactiveIconRes;
            this.activeIconRes = activeIconRes;
            this.label = label;
        }
    }

    // Phương thức để lấy thông tin item
    private NavItemInfo getNavItemInfo(int index) {
        switch (index) {
            case 0: return new NavItemInfo(R.drawable.ic_home_outline, R.drawable.ic_home_filled, "Home");
            case 1: return new NavItemInfo(R.drawable.ic_order_outline, R.drawable.ic_order_filled, "Order");
            case 2: return new NavItemInfo(R.drawable.ic_favorite_outline, R.drawable.ic_favorite_filled, "Favourite");
            case 3: return new NavItemInfo(R.drawable.ic_notifications_outline, R.drawable.ic_notifications_filled, "Notification");
            case 4: return new NavItemInfo(R.drawable.ic_person_outline, R.drawable.ic_person_filled, "Profile");
            default: throw new IllegalArgumentException("Invalid index");
        }
    }

    //  function để lấy class Activity mục tiêu
    private Class<?> getTargetActivity(int index) {
        switch (index) {
            case 0: return MainActivity.class; // Hoặc HomeActivity.class
            // TODO: Tạo và thay thế bằng các class Activity thực tế của bạn
            // case 1: return OrderActivity.class;
            case 2: return FavouriteActivity.class;
            case 3: return NotificationActivity.class;
            // case 4: return ProfileActivity.class;
            default: return null;
        }
    }
}