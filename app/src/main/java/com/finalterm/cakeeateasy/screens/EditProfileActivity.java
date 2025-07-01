package com.finalterm.cakeeateasy.screens;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Customer;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {

    // --- UI Elements ---
    private EditText edtName, edtPhone, edtAddress, edtDob;
    private Button btnSaveChanges;
    private MaterialToolbar toolbar;

    // --- Data & Logic ---
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;
    private Customer currentUser;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        dbHelper = new DatabaseHelper(this);

        initViews(); // Gọi initViews trước
        loadCurrentUserId();
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadAndDisplayUserData();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        edtName = findViewById(R.id.edt_full_name);
        edtPhone = findViewById(R.id.edt_phone_number);
        edtAddress = findViewById(R.id.edt_address_detail); // BỎ COMMENT VÀ SỬA ID
        edtDob = findViewById(R.id.edt_dob);
        btnSaveChanges = findViewById(R.id.btn_save_changes);

        // Không cần tìm txtEmail và txtUsername vì chúng không có trong layout
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    private void loadAndDisplayUserData() {
        currentUser = dbHelper.getCustomerById(currentUserId);
        if (currentUser != null) {
            edtName.setText(currentUser.getName());
            edtPhone.setText(currentUser.getPhone());
            // Dòng này bây giờ sẽ chạy được
            edtAddress.setText(currentUser.getAddress());

            // Xóa các dòng setText cho txtEmail và txtUsername
            // txtEmail.setText(currentUser.getEmail());
            // txtUsername.setText(currentUser.getUsername());

            String dobString = currentUser.getDob();
            if (dobString != null && !dobString.equalsIgnoreCase("NULL")) {
                edtDob.setText(dobString);
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    Date date = sdf.parse(dobString);
                    if (date != null) {
                        myCalendar.setTime(date);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        } else {
            Toast.makeText(this, "Không thể tải thông tin cá nhân.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupListeners() {
        toolbar.setNavigationOnClickListener(v -> finish());
        btnSaveChanges.setOnClickListener(v -> saveProfileChanges());

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

        edtDob.setOnClickListener(v -> new DatePickerDialog(EditProfileActivity.this, dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show());
    }

    private void updateDobLabel() {
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDob.setText(sdf.format(myCalendar.getTime()));
    }

    private void saveProfileChanges() {
        String name = edtName.getText().toString().trim();
        String phone = edtPhone.getText().toString().trim();
        String address = edtAddress.getText().toString().trim();
        String dob = edtDob.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(address)) {
            Toast.makeText(this, "Vui lòng không để trống các trường bắt buộc.", Toast.LENGTH_SHORT).show();
            return;
        }

        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setAddress(address);
        currentUser.setDob(dob);

        boolean isSuccess = dbHelper.updateUserProfile(currentUser);

        if (isSuccess) {
            Toast.makeText(this, "Cập nhật thông tin thành công!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Cập nhật thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
        }
    }
}