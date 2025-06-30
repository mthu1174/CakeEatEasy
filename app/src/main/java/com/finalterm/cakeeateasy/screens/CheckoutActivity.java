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
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Address;
import com.finalterm.cakeeateasy.models.CartItem;
import com.finalterm.cakeeateasy.models.CartManager;

import java.util.ArrayList;
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
    private boolean fromCartSelection = false;
    private boolean fromBuyNow = false;
    private List<CartItem> originalCartItems = new java.util.ArrayList<>();
    private int shippingFee = 35000; // Default shipping fee
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        // Init DB Helper
        dbHelper = new DatabaseHelper(this);

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
        fromCartSelection = getIntent().getBooleanExtra("from_cart_selection", false);
        fromBuyNow = getIntent().getBooleanExtra("from_buy_now", false);

        setCartItemsFromIntent();

        displayCartItems();

        // Load default address
        loadDefaultAddress();

        // Get delivery time and voucher from CartManager
        deliveryTime = CartManager.getInstance().getDeliveryTime();
        txtTimeDelivery.setText(deliveryTime);
        voucherAmount = CartManager.getInstance().getVoucherAmount();
        voucherCode = CartManager.getInstance().getVoucherCode();
        String voucherType = CartManager.getInstance().getVoucherType();
        
        // Set shipping fee based on voucher type
        if ("FREESHIP".equals(voucherType)) {
            shippingFee = 0;
        } else {
            shippingFee = 35000; // Default shipping fee
        }
        
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
        btnBack.setOnClickListener(v -> {
            if (fromCartSelection) {
                // Restore original cart items before going back
                CartManager.getInstance().restoreOriginalCart(originalCartItems);
            }
            finish();
        });
        btnEditAddress.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddressActivity.class);
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
            if (fromCartSelection) {
                // For cart selection checkout, remove only the selected items from the cart
                CartManager.getInstance().removeItems(cartItems);
            } else if (fromBuyNow) {
                // For Buy Now checkout, don't modify the cart at all
                // The cart remains unchanged since we didn't add the item to it
            } else {
                // For regular checkout, clear the entire cart
                CartManager.getInstance().clearCart();
            }
            
            Intent intent = new Intent(this, SuccessCheckoutActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @SuppressWarnings("unchecked")
    private void setCartItemsFromIntent() {
        if (fromCartSelection || fromBuyNow) {
            // Get selected items from intent (for cart selection) or buy now items
            List<CartItem> selectedItems = (List<CartItem>) getIntent().getSerializableExtra("selected_items");
            if (selectedItems != null) {
                cartItems = selectedItems;
            }
            // Save original cart items to restore later if needed (only for cart selection)
            if (fromCartSelection) {
                originalCartItems.clear();
                originalCartItems.addAll(CartManager.getInstance().getCartItems());
            }
        } else {
            // For regular checkout (not from cart selection or buy now), use all cart items
            cartItems = CartManager.getInstance().getCartItems();
        }
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
                // Save to CartManager
                CartManager.getInstance().setDeliveryTime(formatted);
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
        
        // Display shipping fee
        if (shippingFee == 0) {
            txtShipping.setText("Free");
        } else {
            txtShipping.setText(String.format("%,d đ", shippingFee));
        }
        
        int total = orderTotal - itemsDiscount - voucherAmount + shippingFee;
        edtTotal.setText(String.format("%,d đ", total));
    }

    private void loadDefaultAddress() {
        ArrayList<Address> addresses = dbHelper.getAllAddresses();
        Address defaultAddress = null;
        if (!addresses.isEmpty()) {
            // The list is sorted by default, so the first one is the default.
            defaultAddress = addresses.get(0);
        }

        if (defaultAddress != null) {
            edtAddressName.setText(defaultAddress.getName());
            edtAddressPhone.setText(defaultAddress.getPhone());
            edtAddressDetail.setText(defaultAddress.getAddressLine());
        } else {
            edtAddressName.setText("No address set");
            edtAddressPhone.setText("");
            edtAddressDetail.setText("Please add a delivery address.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOUCHER_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            voucherCode = data.getStringExtra("voucher_code");
            voucherAmount = data.getIntExtra("voucher_amount", 0);
            String voucherType = data.getStringExtra("voucher_type");

            if ("FREESHIP".equals(voucherType)) {
                shippingFee = 0;
            } else {
                shippingFee = 35000;
            }

            txtVoucher.setText(voucherCode);
            CartManager.getInstance().setVoucher(voucherCode, voucherAmount, voucherType);
            updatePaymentSummary();
        } else if (requestCode == EDIT_ADDRESS_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Address selectedAddress = (Address) data.getSerializableExtra("selected_address");
            if (selectedAddress != null) {
                edtAddressName.setText(selectedAddress.getName());
                edtAddressPhone.setText(selectedAddress.getPhone());
                edtAddressDetail.setText(selectedAddress.getAddressLine());
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fromCartSelection) {
            // Restore original cart items before going back
            CartManager.getInstance().restoreOriginalCart(originalCartItems);
        }
        // For Buy Now, no need to restore anything since we didn't modify the cart
        super.onBackPressed();
    }
}