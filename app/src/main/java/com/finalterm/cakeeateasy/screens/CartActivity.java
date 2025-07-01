package com.finalterm.cakeeateasy.screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.CartAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper; // SỬ DỤNG DATABASE HELPER
import com.finalterm.cakeeateasy.models.CartItem;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

// GIỮ LẠI: Implement interface để lắng nghe sự kiện từ Adapter
// SỬA LẠI: Kế thừa từ AppCompatActivity vì bạn không dùng BaseActivity
public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private static final int VOUCHER_REQUEST_CODE = 1001;

    // --- UI Elements (Khai báo lại cho khớp với XML) ---
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CheckBox checkboxSelectAll;
    private TextView txtTimeDelivery, txtVoucher, txtTotalPrice, txtSave;
    private EditText edtVoucherMinusAmount;
    private Button btnCheckout;
    private ImageView btnBack, btnChat;
    private ImageButton btnTimePicker, btnVoucherPicker;

    // --- Data & Logic ---
    private List<CartItem> cartItems;
    private DatabaseHelper dbHelper;
    private int currentCustomerId = -1; // Sẽ lấy từ SharedPreferences

    private String selectedVoucherCode = "";
    private int selectedVoucherAmount = 0;
    private String selectedVoucherType = "";
    private String deliveryTime = "Schedule date and time"; // Giá trị mặc định

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupRecyclerView();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // GIỮ LẠI: Luôn load lại dữ liệu từ DB khi quay lại màn hình
        loadCartDataFromDb();
    }

    private void initViews() {
        rvCartItems = findViewById(R.id.rv_cart_items);
        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        txtTotalPrice = findViewById(R.id.txt_total_price); // Sửa lại ID và loại View
        edtVoucherMinusAmount = findViewById(R.id.edt_voucher_minus_amount);
        txtVoucher = findViewById(R.id.txt_voucher);
        btnCheckout = findViewById(R.id.btn_checkout);
        btnBack = findViewById(R.id.btn_back);
        btnTimePicker = findViewById(R.id.btn_time_picker);
        txtTimeDelivery = findViewById(R.id.txt_time_delivery);
        btnVoucherPicker = findViewById(R.id.btn_voucher_picker);
//        btnChat = findViewById(R.id.btn_chat); // Thêm nút chat
//        txtSave = findViewById(R.id.txt_save); // Thêm txtSave

        txtTimeDelivery.setText(deliveryTime);
        txtVoucher.setText("Select or enter code");
        edtVoucherMinusAmount.setText("-0 đ");
    }

    private void loadCartDataFromDb() {
        // GIỮ LẠI: Lấy dữ liệu từ Database
        cartItems = dbHelper.getCartItemsByCustomerId(currentCustomerId);
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        // Cập nhật adapter với dữ liệu mới
        cartAdapter.updateCartItems(cartItems);

        // Cập nhật lại tổng tiền và trạng thái checkbox
        updateTotals();
        checkSelectAllState();
    }

    private void setupRecyclerView() {
        cartItems = new ArrayList<>();
        // GIỮ LẠI: Khởi tạo Adapter và truyền "this" làm listener
        cartAdapter = new CartAdapter(this, cartItems, this);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> finish());

        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // isPressed() để tránh vòng lặp khi code tự setChecked
            if (buttonView.isPressed()) {
                for (CartItem item : cartItems) {
                    item.setSelected(isChecked);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotals();
            }
        });

        // KẾT HỢP: Lấy logic của Time Picker từ File 1
        btnTimePicker.setOnClickListener(v -> showDateTimePicker(txtTimeDelivery));

        // KẾT HỢP: Lấy logic của Voucher Picker từ File 1
        btnVoucherPicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent, VOUCHER_REQUEST_CODE);
        });

        btnCheckout.setOnClickListener(v -> {
            // SỬA LẠI DÒNG NÀY: Lấy danh sách đã cập nhật từ Adapter
            List<CartItem> selectedItems = cartAdapter.getCartItems().stream()
                    .filter(CartItem::isSelected)
                    .collect(Collectors.toList());

            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }

            // KẾT HỢP: Truyền dữ liệu sang CheckoutActivity
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("selected_items", (Serializable) selectedItems);
            intent.putExtra("delivery_time", deliveryTime);
            intent.putExtra("voucher_code", selectedVoucherCode);
            startActivity(intent);
        });
    }

    // KẾT HỢP: Lấy hàm `showDateTimePicker` từ File 1
    private void showDateTimePicker(TextView target) {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            date.set(year, month, dayOfMonth);
            new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String formatted = String.format("%02d/%02d/%04d %02d:%02d", dayOfMonth, month + 1, year, hourOfDay, minute);
                target.setText(formatted);
                // Lưu vào biến cục bộ
                this.deliveryTime = formatted;
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true).show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE)).show();
    }

    // KẾT HỢP: Đây là hàm quan trọng nhất, kết hợp logic tính tiền của cả hai file
    private void updateTotals() {
        // 1. Tính tổng tiền hàng
        int sum = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                // Sửa lỗi parsing tiền tệ nếu có dấu phẩy hoặc chữ "đ"
                double price = item.getPrice(); // Giả sử getPrice() trả về double/int
                sum += price * item.getQuantity();
            }
        }

        // 2. Tính phí ship (logic từ File 1)
        int shippingFee = 35000; // Phí ship mặc định
        if ("FREESHIP".equals(selectedVoucherType)) {
            shippingFee = 0;
        }

        // 3. Tính tổng cuối cùng
        int total = sum - selectedVoucherAmount + shippingFee;
        if (total < 0) {
            total = 0; // Đảm bảo tổng tiền không âm
        }

        // 4. Cập nhật UI (Dùng NumberFormat để định dạng tiền tệ cho đẹp)
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtTotalPrice.setText(currencyFormat.format(total));
        edtVoucherMinusAmount.setText(String.format("- %s", currencyFormat.format(selectedVoucherAmount)));
        txtVoucher.setText(selectedVoucherCode.isEmpty() ? "Chọn voucher" : selectedVoucherCode);
    }

    // GIỮ LẠI: Hàm kiểm tra và cập nhật trạng thái của checkbox "Chọn tất cả"
    private void checkSelectAllState() {
        if (cartItems == null || cartItems.isEmpty()) {
            checkboxSelectAll.setChecked(false);
            return;
        }
        boolean allSelected = cartItems.stream().allMatch(CartItem::isSelected);

        checkboxSelectAll.setOnCheckedChangeListener(null); // Tắt listener tạm thời
        checkboxSelectAll.setChecked(allSelected);
        // Bật lại listener
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                for (CartItem item : cartItems) {
                    item.setSelected(isChecked);
                }
                cartAdapter.notifyDataSetChanged();
                updateTotals();
            }
        });
    }

    // KẾT HỢP: Lấy hàm `onActivityResult` từ File 1 để xử lý kết quả trả về từ VoucherActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOUCHER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Lưu thông tin voucher vào các biến cục bộ thay vì CartManager
            this.selectedVoucherAmount = data.getIntExtra("voucher_amount", 0);
            this.selectedVoucherCode = data.getStringExtra("voucher_code");
            this.selectedVoucherType = data.getStringExtra("voucher_type");

            // Cập nhật lại tổng tiền ngay lập tức
            updateTotals();
        }
    }

    // --- Implement các phương thức từ Interface CartItemListener (Giữ nguyên từ File 2) ---

    @Override
    public void onQuantityChanged(CartItem item, int newQuantity) {
        // Cập nhật số lượng trong DB
        dbHelper.updateCartItemQuantity(item.getOrderItemId(), newQuantity);
        // Cập nhật số lượng trong list hiện tại
        item.setQuantity(newQuantity);
        // Cập nhật lại tổng tiền
        updateTotals();
    }

    @Override
    public void onItemRemoved(CartItem item, int position) {
        // Xóa item khỏi DB
        dbHelper.removeCartItem(item.getOrderItemId());
        // Xóa item khỏi list hiện tại
        cartItems.remove(position);
        cartAdapter.notifyItemRemoved(position);
        cartAdapter.notifyItemRangeChanged(position, cartItems.size()); // Cần thiết để cập nhật lại vị trí
        // Cập nhật lại tổng tiền và checkbox
        updateTotals();
        checkSelectAllState();
    }

    @Override
    public void onItemSelectionChanged() {
        // Khi người dùng tick/bỏ tick một item
        updateTotals();
        checkSelectAllState();
    }
}