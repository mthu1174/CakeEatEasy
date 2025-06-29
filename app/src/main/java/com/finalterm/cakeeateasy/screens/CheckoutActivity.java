package com.finalterm.cakeeateasy.screens;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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

import androidx.appcompat.app.AppCompatActivity;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.CartItem;
import com.finalterm.cakeeateasy.models.CartManager;

import java.util.Calendar;
import java.util.List;

public class CheckoutActivity extends AppCompatActivity {
    private static final int VOUCHER_REQUEST_CODE = 1001;
    private static final int EDIT_ADDRESS_REQUEST_CODE = 1002;
    private LinearLayout layoutCheckoutItems;
    private TextView edtAddressName, edtAddressPhone, edtAddressDetail;
    private TextView txtTimeDelivery, txtVoucher;
    private ImageButton btnTimePicker, btnVoucherPicker;
    private ImageView btnBack, btnEditAddress;
    private RadioGroup rgPaymentMethod;
    private Button btnCheckout;
    private TextView edtOrderTotal, edtItemsDiscount, txtCouponDiscount, txtShipping, edtTotal;
    private List<CartItem> cartItems;
    private int voucherAmount = 0;
    private String voucherCode = "";
    private String deliveryTime = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Find views
        layoutCheckoutItems = findViewById(R.id.layout_checkout_items);
        edtAddressName = findViewById(R.id.edt_address_name);
        edtAddressPhone = findViewById(R.id.edt_address_phone);
        edtAddressDetail = findViewById(R.id.edt_address_detail);
        txtTimeDelivery = findViewById(R.id.txt_time_delivery);
        txtVoucher = findViewById(R.id.txt_voucher);
        btnTimePicker = findViewById(R.id.btn_time_picker);
        btnVoucherPicker = findViewById(R.id.btn_voucher_picker);
        btnBack = findViewById(R.id.btn_back);
        btnEditAddress = findViewById(R.id.btn_edit_address);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnCheckout = findViewById(R.id.btn_checkout);
        edtOrderTotal = findViewById(R.id.edt_order_total);
        edtItemsDiscount = findViewById(R.id.edt_items_discount);
        txtCouponDiscount = findViewById(R.id.txt_coupon_discount);
        txtShipping = findViewById(R.id.txt_shipping);
        edtTotal = findViewById(R.id.edt_total);

        // Load cart items
        cartItems = CartManager.getInstance().getCartItems();
        displayCartItems();

        // Load address (stub, you can load from user profile or intent)
        edtAddressName.setText("Linh Nha Nguyen");
        edtAddressPhone.setText("(+84)381 234 567");
        edtAddressDetail.setText("White House, District 1, USA");

        // Always get delivery time and voucher from CartManager (or static fields)
        // For demo, let's assume CartManager has getDeliveryTime() and getVoucherCode().
        // If not, you can add them, or use static fields.
        // For now, use static fields in CheckoutActivity for demo:
        deliveryTime = CartManager.getInstance().deliveryTime != null ? CartManager.getInstance().deliveryTime : "18:00";
        txtTimeDelivery.setText(deliveryTime);
        voucherAmount = CartManager.getInstance().getVoucherAmount();
        voucherCode = CartManager.getInstance().voucherCode != null ? CartManager.getInstance().voucherCode : "";
        txtVoucher.setText(voucherCode.isEmpty() ? "Select or enter code" : voucherCode);

        // Payment method: only one can be selected (RadioGroup handles this), but if nested, enforce in code
        RadioButton rbBank = findViewById(R.id.bank_account);
        RadioButton rbVisa = findViewById(R.id.rb_visa);
        RadioButton rbMomo = findViewById(R.id.rb_momo);
        rbBank.setChecked(true);
        rbBank.setOnClickListener(v -> {
            rbBank.setChecked(true);
            rbVisa.setChecked(false);
            rbMomo.setChecked(false);
        });
        rbVisa.setOnClickListener(v -> {
            rbBank.setChecked(false);
            rbVisa.setChecked(true);
            rbMomo.setChecked(false);
        });
        rbMomo.setOnClickListener(v -> {
            rbBank.setChecked(false);
            rbVisa.setChecked(false);
            rbMomo.setChecked(true);
        });

        // Calculate and display payment summary
        updatePaymentSummary();

        // Navigation
        btnBack.setOnClickListener(v -> finish());
        btnEditAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditAddressActivity.class);
            startActivityForResult(intent, EDIT_ADDRESS_REQUEST_CODE);
        });

        // Time picker
        btnTimePicker.setOnClickListener(v -> showDateTimePicker());

        // Voucher picker
        btnVoucherPicker.setOnClickListener(v -> {
            Intent intent = new Intent(this, VoucherActivity.class);
            startActivityForResult(intent, VOUCHER_REQUEST_CODE);
        });

        // Checkout
        btnCheckout.setOnClickListener(v -> {
            // You can add order saving logic here
            CartManager.getInstance().clearCart();
            Intent intent = new Intent(this, SuccessCheckoutActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void displayCartItems() {
        layoutCheckoutItems.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);
        for (CartItem item : cartItems) {
            View itemView = inflater.inflate(R.layout.item_checkout_product, layoutCheckoutItems, false);
            // Set product info (findViewById on itemView)
            EditText name = itemView.findViewById(R.id.edt_name_product_checkout);
            EditText currentPrice = itemView.findViewById(R.id.current_price_checkout);
            EditText discount = itemView.findViewById(R.id.edt_discount_percent_checkout);
            EditText firstPrice = itemView.findViewById(R.id.edt_first_price_checkout);
            EditText quantity = itemView.findViewById(R.id.edt_quantity);
            ImageView img = itemView.findViewById(R.id.img_product_checkout);
            name.setText(item.getName());
            currentPrice.setText(item.getPrice());
            discount.setText(item.getDiscount());
            // Calculate first price from price and discount
            try {
                int price = Integer.parseInt(item.getPrice().replaceAll("[^\\d]", ""));
                int discountPercent = 0;
                if (item.getDiscount().contains("%")) {
                    discountPercent = Integer.parseInt(item.getDiscount().replaceAll("[^\\d]", ""));
                }
                int firstPriceValue = price;
                if (discountPercent > 0) {
                    firstPriceValue = (int)(price * 100.0 / (100 - discountPercent));
                }
                firstPrice.setText(String.format("%,d đ", firstPriceValue));
            } catch (Exception e) {
                firstPrice.setText("");
            }
            quantity.setText("x" + item.getQuantity());
            img.setImageResource(item.getImageResId());
            layoutCheckoutItems.addView(itemView);
        }
    }

    private void showDateTimePicker() {
        final Calendar currentDate = Calendar.getInstance();
        final Calendar date = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            date.set(year, month, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                date.set(Calendar.HOUR_OF_DAY, hourOfDay);
                date.set(Calendar.MINUTE, minute);
                String formatted = String.format("%02d/%02d/%04d %02d:%02d", dayOfMonth, month+1, year, hourOfDay, minute);
                deliveryTime = formatted;
                txtTimeDelivery.setText(formatted);
            }, currentDate.get(Calendar.HOUR_OF_DAY), currentDate.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DATE));
        datePickerDialog.show();
    }

    private void updatePaymentSummary() {
        int orderTotal = 0;
        int itemsDiscount = 0;
        for (CartItem item : cartItems) {
            int price = 0;
            int firstPrice = 0;
            int discountPercent = 0;
            try {
                price = Integer.parseInt(item.getPrice().replaceAll("[^\\d]", ""));
                if (item.getDiscount().contains("%")) {
                    discountPercent = Integer.parseInt(item.getDiscount().replaceAll("[^\\d]", ""));
                }
                if (discountPercent > 0) {
                    firstPrice = (int)(price * 100.0 / (100 - discountPercent));
                } else {
                    firstPrice = price;
                }
            } catch (Exception e) {
                firstPrice = price;
            }
            orderTotal += firstPrice * item.getQuantity();
            itemsDiscount += (firstPrice - price) * item.getQuantity();
        }
        edtOrderTotal.setText(String.format("%,d đ", orderTotal));
        edtItemsDiscount.setText(String.format("-%,d đ", itemsDiscount));
        txtCouponDiscount.setText(String.format("-%,d đ", voucherAmount));
        txtShipping.setText("Free");
        int total = orderTotal - itemsDiscount - voucherAmount;
        edtTotal.setText(String.format("%,d đ", total));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOUCHER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            voucherAmount = data.getIntExtra("voucher_amount", 0);
            voucherCode = data.getStringExtra("voucher_code");
            txtVoucher.setText(voucherCode);
            CartManager.getInstance().setVoucherAmount(voucherAmount);
            updatePaymentSummary();
        } else if (requestCode == EDIT_ADDRESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            // Update address info if needed
            String name = data.getStringExtra("address_name");
            String phone = data.getStringExtra("address_phone");
            String detail = data.getStringExtra("address_detail");
            if (name != null) edtAddressName.setText(name);
            if (phone != null) edtAddressPhone.setText(phone);
            if (detail != null) edtAddressDetail.setText(detail);
        }
    }
}