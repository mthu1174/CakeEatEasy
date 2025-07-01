package com.finalterm.cakeeateasy.screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.CartItem;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {
    // --- Constants ---
    private static final int VOUCHER_REQUEST_CODE = 1001;
    private static final int EDIT_ADDRESS_REQUEST_CODE = 1002;

    // --- UI Elements (Đã cập nhật để khớp với XML) ---
    private LinearLayout layoutCheckoutItems;
    private TextView edtAddressName, edtAddressPhone, edtAddressDetail;
    private TextView txtTimeDelivery, txtVoucher;
    private ImageButton btnTimePicker, btnVoucherPicker;
    private ImageView btnBack, btnEditAddress;
    private RadioGroup rgPaymentMethod;
    private Button btnPlaceOrder;
    private TextView txtOrderSubtotal, txtItemsDiscount, txtCouponDiscount, txtShipping, txtTotal;

    // --- Data & Logic ---
    private List<CartItem> itemsToCheckout;
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;

    private int selectedVoucherAmount = 0;
    private String selectedVoucherCode = "";
    private String selectedVoucherType = "";
    private String selectedDeliveryTime = "";
    private String selectedPaymentMethod = ""; // Sẽ được set khi người dùng chọn
    private int shippingFee = 35000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        dbHelper = new DatabaseHelper(this);
        loadCurrentUserId();

        itemsToCheckout = (List<CartItem>) getIntent().getSerializableExtra("selected_items");

        if (currentUserId == -1 || itemsToCheckout == null || itemsToCheckout.isEmpty()) {
            Toast.makeText(this, "Có lỗi xảy ra. Vui lòng thử lại.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
//        loadDefaultData();
        displayCheckoutItems();
        updatePaymentSummary();
        setupListeners();
    }

    private void initViews() {
        // Address & Header
        layoutCheckoutItems = findViewById(R.id.layout_checkout_items);
        edtAddressName = findViewById(R.id.edt_address_name);
        edtAddressPhone = findViewById(R.id.edt_address_phone);
        edtAddressDetail = findViewById(R.id.edt_address_detail);
        btnBack = findViewById(R.id.btn_back);
        findViewById(R.id.btn_edit_address).setOnClickListener(v -> {/* Handle edit address */}); // Gán tạm

        // Time & Voucher
        txtTimeDelivery = findViewById(R.id.txt_time_delivery);
        txtVoucher = findViewById(R.id.txt_voucher);
        btnTimePicker = findViewById(R.id.btn_time_picker);
        btnVoucherPicker = findViewById(R.id.btn_voucher_picker);

        // Payment
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPlaceOrder = findViewById(R.id.btn_checkout);

        // Payment Summary (SỬA LẠI CÁC ID Ở ĐÂY)
        txtOrderSubtotal = findViewById(R.id.edt_order_total);
        txtItemsDiscount = findViewById(R.id.edt_items_discount);
        txtCouponDiscount = findViewById(R.id.txt_coupon_discount);
        txtShipping = findViewById(R.id.txt_shipping);
        txtTotal = findViewById(R.id.edt_total);
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

//    private void loadDefaultData() {
//        // Gọi phương thức mới từ DatabaseHelper
//        Address userAddress = dbHelper.getUserAddress(currentUserId);
//
//        if (userAddress != null) {
//            // Kiểm tra và hiển thị thông tin
//            edtAddressName.setText(TextUtils.isEmpty(userAddress.getName()) ? "Chưa có tên" : userAddress.getName());
//            edtAddressPhone.setText(TextUtils.isEmpty(userAddress.getPhone()) ? "Chưa có SĐT" : userAddress.getPhone());
//            edtAddressDetail.setText(TextUtils.isEmpty(userAddress.getAddressDetail()) ? "Chưa có địa chỉ chi tiết" : userAddress.getAddressDetail());
//        } else {
//            // Xử lý trường hợp không lấy được địa chỉ (user mới, lỗi db,...)
//            edtAddressName.setText("Vui lòng cập nhật");
//            edtAddressPhone.setText("Vui lòng cập nhật");
//            edtAddressDetail.setText("Vui lòng thêm địa chỉ nhận hàng");
//            Toast.makeText(this, "Vui lòng cập nhật địa chỉ của bạn", Toast.LENGTH_SHORT).show();
//        }
//    }

    private void displayCheckoutItems() {
        layoutCheckoutItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (CartItem item : itemsToCheckout) {
            // Sửa lại để inflate layout mới (nếu bạn đã tạo item_checkout_product.xml)
            View itemView = inflater.inflate(R.layout.item_checkout_product, layoutCheckoutItems, false);

            // Sửa lại để tìm đúng ID trong item_checkout_product.xml
            TextView name = itemView.findViewById(R.id.edt_name_product_checkout);
            TextView price = itemView.findViewById(R.id.current_price_checkout);
            TextView quantity = itemView.findViewById(R.id.edt_quantity);
            ImageView img = itemView.findViewById(R.id.img_product_checkout);

            name.setText(item.getName());
            price.setText(currencyFormat.format(item.getPrice()));
            quantity.setText("x" + item.getQuantity());

            Glide.with(this)
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.placeholder_cake_promo)
                    .into(img);

            layoutCheckoutItems.addView(itemView);
        }
    }
    private void updatePaymentSummary() {
        double subtotal = 0;
        // Giả sử không có logic tính discount trên từng item ở đây
        // subtotal là tổng giá các sản phẩm
        for (CartItem item : itemsToCheckout) {
            subtotal += item.getPrice() * item.getQuantity();
        }

        // Cập nhật phí ship dựa trên voucher
        if ("FREESHIP".equals(selectedVoucherType)) {
            shippingFee = 0;
        } else {
            shippingFee = 35000; // Reset về mặc định nếu voucher không phải freeship
        }

        double total = subtotal - selectedVoucherAmount + shippingFee;
        if (total < 0) total = 0;

        // Định dạng và hiển thị
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtOrderSubtotal.setText(currencyFormat.format(subtotal));
        txtItemsDiscount.setText(currencyFormat.format(0)); // Giả sử không có discount này
        txtCouponDiscount.setText(String.format("- %s", currencyFormat.format(selectedVoucherAmount)));

        if (shippingFee == 0) {
            txtShipping.setText("Miễn phí");
        } else {
            txtShipping.setText(currencyFormat.format(shippingFee));
        }

        txtTotal.setText(currencyFormat.format(total));
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnTimePicker.setOnClickListener(v -> showDateTimePicker());

        // === THÊM LẠI LOGIC CHO NÚT VOUCHER ===
        btnVoucherPicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent, VOUCHER_REQUEST_CODE);
        });

        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = findViewById(checkedId);
            if (checkedRadioButton != null) {
                selectedPaymentMethod = checkedRadioButton.getText().toString();
            }
        });

        btnPlaceOrder.setOnClickListener(v -> placeOrder());
    }

    private void placeOrder() {
        double totalAmount = 0;
        for (CartItem item : itemsToCheckout) {
            totalAmount += item.getPrice() * item.getQuantity();
        }
        totalAmount = totalAmount - selectedVoucherAmount + shippingFee;
        if (totalAmount < 0) totalAmount = 0;

        // Lấy thông tin địa chỉ từ TextViews
        String address = edtAddressName.getText().toString() + " - " +
                edtAddressPhone.getText().toString() + "\n" +
                edtAddressDetail.getText().toString();

        // GỌI HÀM CỦA DATABASE HELPER (từ File 2)
        String newOrderId = dbHelper.placeOrder(
                currentUserId,
                itemsToCheckout,
                totalAmount,
                address,
                selectedPaymentMethod,
                selectedDeliveryTime,
                selectedVoucherCode,
                shippingFee
        );

        if (!TextUtils.isEmpty(newOrderId)) {
            Toast.makeText(this, "Đặt hàng thành công! Mã đơn hàng: " + newOrderId, Toast.LENGTH_LONG).show();

            // Chuyển sang màn hình thành công và xóa stack cũ
            Intent intent = new Intent(this, SuccessCheckoutActivity.class);
            intent.putExtra("ORDER_ID", newOrderId);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Đặt hàng thất bại, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            Calendar date = Calendar.getInstance();
            date.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                selectedDeliveryTime = String.format("%02d/%02d/%04d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute);
                txtTimeDelivery.setText(selectedDeliveryTime);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || data == null) return;

        if (requestCode == VOUCHER_REQUEST_CODE) {
            selectedVoucherAmount = data.getIntExtra("voucher_amount", 0);
            selectedVoucherCode = data.getStringExtra("voucher_code");
            selectedVoucherType = data.getStringExtra("voucher_type");

            // Cập nhật lại giao diện
            txtVoucher.setText(TextUtils.isEmpty(selectedVoucherCode) ? "Chọn voucher" : selectedVoucherCode);
            updatePaymentSummary();
        }
        // Bạn có thể thêm else if cho EDIT_ADDRESS_REQUEST_CODE ở đây
    }
}

