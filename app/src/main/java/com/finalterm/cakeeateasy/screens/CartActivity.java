package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Button;
import java.util.Calendar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finalterm.cakeeateasy.adapters.CartAdapter;
import com.finalterm.cakeeateasy.models.CartItem;
import com.finalterm.cakeeateasy.models.CartManager;
import android.widget.CheckBox;
import android.widget.EditText;
import java.util.List;
import android.widget.ImageView;

public class CartActivity extends AppCompatActivity {

    private static final int VOUCHER_REQUEST_CODE = 1001;
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CheckBox checkboxSelectAll;
    private EditText edtTotal, edtVoucherMinusAmount;
    private TextView txtVoucher;
    private List<CartItem> cartItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        rvCartItems = findViewById(R.id.rv_cart_items);
        cartItems = CartManager.getInstance().getCartItems();
        cartAdapter = new CartAdapter(cartItems, this::updateTotals);
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        rvCartItems.setAdapter(cartAdapter);

        // Back button navigation to ProductDetailsActivity
        ImageView btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProductDetailsActivity.class);
            startActivity(intent);
            finish();
        });

        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) item.setSelected(isChecked);
            cartAdapter.notifyDataSetChanged();
            updateTotals();
        });

        ImageButton btnTimePicker = findViewById(R.id.btn_time_picker);
        TextView txtTimeDelivery = findViewById(R.id.txt_time_delivery);
        
        // Initialize delivery time display
        String deliveryTime = CartManager.getInstance().getDeliveryTime();
        if (!deliveryTime.equals("18:00")) {
            txtTimeDelivery.setText(deliveryTime);
        }
        
        btnTimePicker.setOnClickListener(v -> showDateTimePicker(txtTimeDelivery));

        ImageButton btnVoucherPicker = findViewById(R.id.btn_voucher_picker);
        btnVoucherPicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent, VOUCHER_REQUEST_CODE);
        });

        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> {
            // Get selected items
            List<CartItem> selectedItems = getSelectedItems();
            if (selectedItems.isEmpty()) {
                // Show a message if no items are selected
                android.widget.Toast.makeText(this, "Please select at least one item", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Navigate to checkout with selected items
            Intent intent = new Intent(this, CheckoutActivity.class);
            intent.putExtra("from_cart_selection", true);
            intent.putExtra("selected_items_count", selectedItems.size());
            
            // Pass selected items as serializable data
            intent.putExtra("selected_items", new java.util.ArrayList<>(selectedItems));
            startActivity(intent);
        });

        edtTotal = findViewById(R.id.edt_total);
        edtVoucherMinusAmount = findViewById(R.id.edt_voucher_minus_amount);
        txtVoucher = findViewById(R.id.txt_voucher);

        // Initialize voucher display
        String voucherCode = CartManager.getInstance().getVoucherCode();
        if (!voucherCode.isEmpty()) {
            txtVoucher.setText(voucherCode);
        }
        
        // Initialize voucher amount display
        int voucherAmount = CartManager.getInstance().getVoucherAmount();
        if (voucherAmount > 0) {
            edtVoucherMinusAmount.setText(String.format("-%,d đ", voucherAmount));
        }

        updateTotals();
    }

    private void showDateTimePicker(TextView target) {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            date.set(year, month, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String formatted = String.format("%02d/%02d/%04d %02d:%02d", dayOfMonth, month+1, year, hourOfDay, minute);
                target.setText(formatted);
                CartManager.getInstance().setDeliveryTime(formatted);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void updateTotals() {
        int sum = 0;
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                String priceStr = item.getPrice().replaceAll("[^\\d]", "");
                int price = priceStr.isEmpty() ? 0 : Integer.parseInt(priceStr);
                sum += price * item.getQuantity();
            }
        }
        int voucher = CartManager.getInstance().getVoucherAmount();
        String voucherType = CartManager.getInstance().getVoucherType();
        
        // Calculate shipping fee
        int shippingFee = 35000; // Default shipping fee
        if ("FREESHIP".equals(voucherType)) {
            shippingFee = 0;
        }
        
        int total = sum - voucher + shippingFee;
        edtTotal.setText(String.format("%,d đ", total));
        edtVoucherMinusAmount.setText(String.format("-%,d đ", voucher));
        txtVoucher.setText(CartManager.getInstance().getVoucherCode());
    }

    private List<CartItem> getSelectedItems() {
        List<CartItem> selectedItems = new java.util.ArrayList<>();
        for (CartItem item : cartItems) {
            if (item.isSelected()) {
                selectedItems.add(item);
            }
        }
        return selectedItems;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOUCHER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int voucherAmount = data.getIntExtra("voucher_amount", 0);
            String voucherCode = data.getStringExtra("voucher_code");
            String voucherType = data.getStringExtra("voucher_type");
            CartManager.getInstance().setVoucherAmount(voucherAmount);
            CartManager.getInstance().setVoucherCode(voucherCode);
            CartManager.getInstance().setVoucherType(voucherType);
            edtVoucherMinusAmount.setText(String.format("-%,d đ", voucherAmount));
            txtVoucher.setText(voucherCode);
            updateTotals();
        }
    }
}