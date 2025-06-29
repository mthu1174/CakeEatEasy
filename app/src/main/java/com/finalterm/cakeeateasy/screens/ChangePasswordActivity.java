package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputEditText;

import com.finalterm.cakeeateasy.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText edtNewPassword;
    private TextInputEditText edtConfirmPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        edtNewPassword = findViewById(R.id.edt_new_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        // Xử lý nút back trên toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // Xử lý nút Confirm
        findViewById(R.id.btn_confirm).setOnClickListener(v -> handleChangePassword());
    }

    private void handleChangePassword() {
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        // Kiểm tra dữ liệu
        if (newPass.isEmpty()) {
            edtNewPassword.setError("Password cannot be empty");
            return;
        }

        if (newPass.length() < 6) {
            edtNewPassword.setError("Password must be at least 6 characters");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            edtConfirmPassword.setError("Passwords do not match");
            return;
        }

        // Nếu mọi thứ hợp lệ
        // TODO: Gọi API để thay đổi mật khẩu trên server
        Toast.makeText(this, "Password changed successfully!", Toast.LENGTH_SHORT).show();
        finish(); // Đóng màn hình sau khi thành công
    }
}