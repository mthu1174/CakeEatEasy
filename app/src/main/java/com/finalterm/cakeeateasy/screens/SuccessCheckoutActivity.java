package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.finalterm.cakeeateasy.R;

public class SuccessCheckoutActivity extends AppCompatActivity {

    // --- UI Elements ---
    private TextView tvInvoiceNumber;
    private TextView tvSuccessMessage;
    private Button btnContinueShopping;
    private Button btnTrackOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_checkout);

        initViews();
        displayOrderInfo();
        setupClickListeners();
        setupBackButtonHandler();
    }

    private void initViews() {
        tvInvoiceNumber = findViewById(R.id.tv_invoice);
        tvSuccessMessage = findViewById(R.id.txt_thank_you_invoice);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);
//        btnTrackOrder = findViewById(R.id.btn_track_order); // Giả sử layout có nút này
    }

    private void displayOrderInfo() {
        // Nhận mã đơn hàng (dạng String) từ Intent mà CheckoutActivity đã gửi
        String orderId = getIntent().getStringExtra("ORDER_ID");

        if (!TextUtils.isEmpty(orderId)) {
            String displayId = "#" + orderId;
            tvInvoiceNumber.setText(displayId);
//            tvSuccessMessage.setText(getString(R.string.success_checkout_message, displayId));
        } else {
            // Trường hợp lỗi: không nhận được mã đơn hàng
            tvInvoiceNumber.setText("#LỖI");
            tvSuccessMessage.setText("Đơn hàng của bạn đã được ghi nhận. Vui lòng kiểm tra trong mục Đơn hàng của tôi.");
        }
    }

    private void setupClickListeners() {
        // Nút "Tiếp tục mua sắm" -> Về màn hình chính
        btnContinueShopping.setOnClickListener(v -> navigateToMain());

        // Nút "Theo dõi đơn hàng" -> Đến màn hình Lịch sử đơn hàng
        btnTrackOrder.setOnClickListener(v -> navigateToOrders());
    }

    /**
     * BƯỚC 2: Ghi đè hành vi của nút Back vật lý.
     * Người dùng không thể quay lại màn hình Checkout.
     */
    private void setupBackButtonHandler() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Khi người dùng nhấn back, thực hiện hành động giống như nhấn nút "Continue Shopping"
                navigateToMain();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        // Xóa tất cả các activity cũ trên stack và tạo một task mới cho MainActivity
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish(); // Đảm bảo activity này cũng được đóng
    }

    private void navigateToOrders() {
        Intent intent = new Intent(this, OrderActivity.class);
        // Không cần xóa stack, để người dùng có thể quay lại MainActivity từ OrderActivity
        startActivity(intent);
        finish(); // Đóng màn hình này
    }
}