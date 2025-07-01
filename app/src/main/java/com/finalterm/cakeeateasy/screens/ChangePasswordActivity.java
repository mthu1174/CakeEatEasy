package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper; // Import DatabaseHelper

public class ChangePasswordActivity extends AppCompatActivity {

    // --- UI Elements ---
    // BƯỚC 1: Thêm ô nhập mật khẩu cũ
    private TextInputEditText edtOldPassword;
    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmPassword;
    private MaterialToolbar toolbar;
    private Button btnConfirm;

    // --- Data & Logic ---
    // BƯỚC 2: Thêm DatabaseHelper và biến lưu ID người dùng
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        dbHelper = new DatabaseHelper(this);

        // BƯỚC 3: Lấy ID của người dùng đang đăng nhập
        loadCurrentUserId();

        // Nếu không có người dùng nào đăng nhập, không cho phép đổi mật khẩu
        if (currentUserId == -1) {
            Toast.makeText(this, "Lỗi: Không tìm thấy thông tin người dùng.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        initViews();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        // Giả sử layout của bạn có thêm id: edt_old_password
        edtOldPassword = findViewById(R.id.edt_old_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnConfirm = findViewById(R.id.btn_confirm);
    }

    private void setupListeners() {
        // Xử lý nút back trên toolbar một cách chuẩn hơn
        toolbar.setNavigationOnClickListener(v -> finish());
        // Xử lý nút Confirm
        btnConfirm.setOnClickListener(v -> handleChangePassword());
    }

    /**
     * Lấy user ID từ SharedPreferences.
     * Bạn cần đảm bảo đã lưu ID này sau khi người dùng đăng nhập.
     */
    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        // Giá trị mặc định là -1 nếu không tìm thấy key "userId"
        this.currentUserId = prefs.getInt("userId", -1);
    }

    private void handleChangePassword() {
        String oldPass = edtOldPassword.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        // Gom hết các bước kiểm tra vào một hàm riêng cho gọn
        if (!isInputValid(oldPass, newPass, confirmPass)) {
            return; // Dừng lại nếu dữ liệu nhập không hợp lệ
        }

        // BƯỚC 4: Tích hợp với Database để thay đổi mật khẩu
        // Giả sử bạn có hàm này trong DatabaseHelper
        boolean isSuccess = dbHelper.checkAndUpdatePassword(currentUserId, oldPass, newPass);

        // BƯỚC 5: Cải thiện phản hồi dựa trên kết quả từ DB
        if (isSuccess) {
            Toast.makeText(this, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng màn hình sau khi thành công
        } else {
            // Có thể do nhập sai mật khẩu cũ hoặc lỗi DB
            Toast.makeText(this, "Đổi mật khẩu thất bại. Vui lòng kiểm tra lại mật khẩu cũ.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Kiểm tra tất cả các điều kiện của dữ liệu đầu vào.
     * @return true nếu tất cả hợp lệ, ngược lại trả về false.
     */
    private boolean isInputValid(String oldPass, String newPass, String confirmPass) {
        // Kiểm tra mật khẩu cũ
        if (TextUtils.isEmpty(oldPass)) {
            edtOldPassword.setError("Vui lòng nhập mật khẩu hiện tại");
            edtOldPassword.requestFocus();
            return false;
        } else {
            edtOldPassword.setError(null);
        }

        // Kiểm tra mật khẩu mới
        if (TextUtils.isEmpty(newPass)) {
            edtNewPassword.setError("Mật khẩu mới không được để trống");
            edtNewPassword.requestFocus();
            return false;
        }

        if (newPass.length() < 6) {
            edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtNewPassword.requestFocus();
            return false;
        } else {
            edtNewPassword.setError(null);
        }

        // Kiểm tra xác nhận mật khẩu
        if (!newPass.equals(confirmPass)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtConfirmPassword.requestFocus();
            return false;
        } else {
            edtConfirmPassword.setError(null);
        }

        return true; // Tất cả đều hợp lệ
    }
}