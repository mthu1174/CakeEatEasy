package com.finalterm.cakeeateasy.screens; // Thay bằng package của bạn

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R; // Thay bằng package của bạn
import com.finalterm.cakeeateasy.screens.dialogs.ChatDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Arrays;
import java.util.List;

public abstract class BaseActivity extends AppCompatActivity {

    private List<View> navItems;
    // Biến cờ để ngăn hiệu ứng chạy khi app vừa khởi động
    private boolean isFirstTime = true;

    public abstract int getNavItemIndex();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.base_root_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0);
            return insets;
        });

        setupNavItems();
        setupNavClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Chỉ áp dụng hiệu ứng chuyển cảnh sau lần khởi động đầu tiên
        if (!isFirstTime) {
            // Áp dụng hiệu ứng fade-in cho Activity vừa được đưa lên trên
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        }
        // Đánh dấu là đã qua lần khởi động đầu tiên
        isFirstTime = false;

        updateNavState();
    }

    protected void setContentLayout(int layoutResID) {
        FrameLayout contentFrame = findViewById(R.id.content_frame);
        contentFrame.removeAllViews();
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
            navItems.get(i).setOnClickListener(v -> navigateTo(index));
        }
        findViewById(R.id.fab_chat).setOnClickListener(v -> openChatDialog());
    }

    private void navigateTo(int index) {
        if (index != getNavItemIndex()) {
            Class<?> targetActivityClass = getTargetActivity(index);
            if (targetActivityClass != null) {
                Intent intent = new Intent(this, targetActivityClass);
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivity(intent);

                // Vẫn giữ dòng này để xử lý cho lần đầu tiên một Activity được tạo
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        }
    }

    private void updateNavState() {
        if (navItems == null) return;
        for (int i = 0; i < navItems.size(); i++) {
            setItemState(navItems.get(i), i, i == getNavItemIndex());
        }
    }

    private void setItemState(View item, int index, boolean isActive) {
        View activeStateLayout = item.findViewById(R.id.nav_item_active);
        ImageView inactiveIcon = item.findViewById(R.id.nav_item_inactive);
        FloatingActionButton activeFab = activeStateLayout.findViewById(R.id.nav_item_active_fab);
        TextView label = activeStateLayout.findViewById(R.id.nav_item_active_label);

        NavItemInfo info = getNavItemInfo(index);

        activeFab.setImageResource(info.activeIconRes);
        label.setText(info.label);
        inactiveIcon.setImageResource(info.inactiveIconRes);

        if (isActive) {
            activeStateLayout.setVisibility(View.VISIBLE);
            inactiveIcon.setVisibility(View.INVISIBLE);
        } else {
            activeStateLayout.setVisibility(View.INVISIBLE);
            inactiveIcon.setVisibility(View.VISIBLE);
        }
    }

    private void openChatDialog() {
        ChatDialogFragment chatDialog = new ChatDialogFragment();
        chatDialog.show(getSupportFragmentManager(), "ChatDialogFragment");
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

    // "Bảng chỉ đường" để lấy class Activity mục tiêu
    private Class<?> getTargetActivity(int index) {
        switch (index) {
            case 0: return MainActivity.class;
            //case 1: return OrderActivity.class; // Nhớ bỏ comment dòng này khi bạn tạo OrderActivity
            case 2: return FavouriteActivity.class;
            case 3: return NotificationActivity.class;
            case 4: return ProfileActivity.class;
            default: return null;
        }
    }
}