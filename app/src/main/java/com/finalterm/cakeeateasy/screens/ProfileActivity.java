package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Customer;

public class ProfileActivity extends BaseActivity {

    // --- UI Elements ---
    private TextView txtUserName, txtUserEmail;
    private Button btnLogout;

    // --- Data & Logic ---
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;

    @Override
    public int getNavItemIndex() {
        return 4; // Vị trí của tab "Profile"
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        initViews();
        loadCurrentUserId();

        // Nếu không có người dùng đăng nhập, không thực hiện gì thêm
        if (currentUserId == -1) {
            // Có thể hiển thị trạng thái "Guest" hoặc điều hướng về Login
            Toast.makeText(this, "Vui lòng đăng nhập.", Toast.LENGTH_SHORT).show();
            // Điều hướng về Login và xóa các màn hình cũ
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }

        // BƯỚC 1: Tải và hiển thị thông tin người dùng
        loadAndDisplayUserInfo();

        setupOptionItems();
        setupClickListeners();
    }

    private void initViews() {
        txtUserName = findViewById(R.id.txt_user_name); // Giả sử có ID này trong card_user_info
        txtUserEmail = findViewById(R.id.txt_user_email); // Giả sử có ID này
        btnLogout = findViewById(R.id.btn_logout);
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    private void loadAndDisplayUserInfo() {
        Customer customer = dbHelper.getCustomerById(currentUserId);
        if (customer != null) {
            txtUserName.setText(customer.getName());
            txtUserEmail.setText(customer.getEmail());
        }
    }

    private void setupOptionItems() {
        setupOptionItem(R.id.option_payment, R.drawable.ic_shopping_cart, "Thanh toán & Mua hàng");
        setupOptionItem(R.id.option_notifications, R.drawable.ic_notifications_outline, "Thông báo");
        setupOptionItem(R.id.option_language, R.drawable.ic_language, "Ngôn ngữ");
        setupOptionItem(R.id.option_security, R.drawable.ic_security, "Bảo mật");
        setupOptionItem(R.id.option_history, R.drawable.ic_order_history, "Lịch sử đơn hàng");
        setupOptionItem(R.id.option_help, R.drawable.ic_help, "Trung tâm Trợ giúp");
    }

    private void setupClickListeners() {
        findViewById(R.id.card_user_info).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, EditProfileActivity.class)));

        // BƯỚC 3: Hoàn thiện các điều hướng
        findViewById(R.id.option_notifications).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, NotificationActivity.class)));

        findViewById(R.id.option_history).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, OrderActivity.class)));

        findViewById(R.id.option_security).setOnClickListener(v ->
                startActivity(new Intent(ProfileActivity.this, ChangePasswordActivity.class)));

        findViewById(R.id.option_payment).setOnClickListener(v -> showToast("Tính năng sắp ra mắt"));
        findViewById(R.id.option_language).setOnClickListener(v -> showToast("Tính năng sắp ra mắt"));
        findViewById(R.id.option_help).setOnClickListener(v -> showToast("Tính năng sắp ra mắt"));

        // BƯỚC 2: Implement chức năng Logout
        btnLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }

    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất không?")
                .setPositiveButton("Đồng ý", (dialog, which) -> {
                    performLogout();
                })
                .setNegativeButton("Hủy", null)
                .setIcon(R.drawable.ic_logout) // Tùy chọn: thêm icon cho dialog
                .show();
    }

    private void performLogout() {
        // Xóa session người dùng
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear(); // Xóa hết dữ liệu (userId, isLoggedIn)
        editor.apply();

        // Điều hướng về màn hình Login và xóa hết các màn hình cũ khỏi stack
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đóng ProfileActivity
    }

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