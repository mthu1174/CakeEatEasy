package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.finalterm.cakeeateasy.R;

public class ProfileActivity extends BaseActivity {

    @Override
    public int getNavItemIndex() {
        return 4;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_profile);

        // Thiết lập các item tùy chọn
        setupOptionItem(R.id.option_payment, R.drawable.ic_shopping_cart, "Payments & Purchases");
        setupOptionItem(R.id.option_notifications, R.drawable.ic_notifications_outline, "Notifications");
        setupOptionItem(R.id.option_language, R.drawable.ic_language, "Language");
        setupOptionItem(R.id.option_security, R.drawable.ic_security, "Security");
        setupOptionItem(R.id.option_history, R.drawable.ic_order_history, "History order");
        setupOptionItem(R.id.option_help, R.drawable.ic_help, "Help Center");

        // Gán sự kiện click
        findViewById(R.id.card_user_info).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));

        findViewById(R.id.option_payment).setOnClickListener(v -> showToast("Payments & Purchases clicked"));
        findViewById(R.id.option_security).setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.btn_logout).setOnClickListener(v -> showToast("Logout clicked"));

    }

    // Hàm helper mới
    private void setupOptionItem(int viewId, int iconRes, String text) {
        ProfileOptionView optionView = findViewById(viewId);
        if (optionView != null) {
            optionView.setOptionIcon(iconRes);
            optionView.setOptionName(text);
        }
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}