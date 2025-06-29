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

public class CartActivity extends AppCompatActivity {

    private static final int VOUCHER_REQUEST_CODE = 1001;
    private RecyclerView rvCartItems;
    private CartAdapter cartAdapter;
    private CheckBox checkboxSelectAll;
    private EditText edtTotal, edtVoucherMinusAmount;
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

        checkboxSelectAll = findViewById(R.id.checkbox_select_all);
        checkboxSelectAll.setOnCheckedChangeListener((buttonView, isChecked) -> {
            for (CartItem item : cartItems) item.setSelected(isChecked);
            cartAdapter.notifyDataSetChanged();
            updateTotals();
        });

        ImageButton btnTimePicker = findViewById(R.id.btn_time_picker);
        TextView txtTimeDelivery = findViewById(R.id.txt_time_delivery);
        btnTimePicker.setOnClickListener(v -> showDateTimePicker(txtTimeDelivery));

        ImageButton btnVoucherPicker = findViewById(R.id.btn_voucher_picker);
        btnVoucherPicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent, VOUCHER_REQUEST_CODE);
        });

        Button btnCheckout = findViewById(R.id.btn_checkout);
        btnCheckout.setOnClickListener(v -> {
            startActivity(new Intent(this, CheckoutActivity.class));
        });

        edtTotal = findViewById(R.id.edt_total);
        edtVoucherMinusAmount = findViewById(R.id.edt_voucher_minus_amount);

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
                String formatted = String.format("%02d:%02d:%04d %02d:%02d", dayOfMonth, month+1, year, hourOfDay, minute);
                target.setText(formatted);
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
        edtTotal.setText(String.format("%,d đ", sum - voucher));
        edtVoucherMinusAmount.setText(String.format("-%,d", voucher));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOUCHER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            int voucherAmount = data.getIntExtra("voucher_amount", 0);
            String voucherCode = data.getStringExtra("voucher_code");
            CartManager.getInstance().setVoucherAmount(voucherAmount);
            edtVoucherMinusAmount.setText(voucherCode);
            updateTotals();
        }
    }
}