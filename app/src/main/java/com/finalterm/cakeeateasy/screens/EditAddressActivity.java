package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Address;

public class EditAddressActivity extends AppCompatActivity {

    private EditText etName, etPhone, etAddress;
    private SwitchCompat swDefault;
    private Button btnDone;
    private ImageView btnBack;
    private DatabaseHelper dbHelper;
    private Address currentAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_address);

        initializeViews();
        loadExistingAddress();
        setupListeners();
    }

    private void initializeViews() {
        dbHelper = new DatabaseHelper(this);
        etName = findViewById(R.id.et_full_name);
        etPhone = findViewById(R.id.et_phone);
        etAddress = findViewById(R.id.et_address);
        swDefault = findViewById(R.id.switch_default);
        btnDone = findViewById(R.id.btn_done);
        btnBack = findViewById(R.id.btn_back);
    }

    private void loadExistingAddress() {
        Intent intent = getIntent();
        if (intent.hasExtra("address_to_edit")) {
            currentAddress = (Address) intent.getSerializableExtra("address_to_edit");
            etName.setText(currentAddress.getName());
            etPhone.setText(currentAddress.getPhone());
            etAddress.setText(currentAddress.getAddressLine());
            swDefault.setChecked(currentAddress.isDefault());
        }
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> finish());
        btnDone.setOnClickListener(v -> saveAddress());
    }

    private void saveAddress() {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String addressLine = etAddress.getText().toString().trim();
        boolean isDefault = swDefault.isChecked();

        if (name.isEmpty() || phone.isEmpty() || addressLine.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currentAddress == null) {
            currentAddress = new Address(0, name, phone, addressLine, isDefault);
            long id = dbHelper.addAddress(currentAddress);
            currentAddress.setId((int) id);
        } else {
            currentAddress.setName(name);
            currentAddress.setPhone(phone);
            currentAddress.setAddressLine(addressLine);
            currentAddress.setDefault(isDefault);
            dbHelper.updateAddress(currentAddress);
        }

        if (isDefault) {
            dbHelper.setDefaultAddress(currentAddress.getId());
        }

        Toast.makeText(this, "Address saved", Toast.LENGTH_SHORT).show();
        finish();
    }
}