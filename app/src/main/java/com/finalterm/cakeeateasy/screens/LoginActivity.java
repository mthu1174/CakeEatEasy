package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // --- UI Elements ---
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin;
    private TextView txtForgotPassword, txtSignup;
    private CheckBox checkBoxRemember;

    // --- Data & Logic ---
    private DatabaseHelper dbHelper;

    // TỐI ƯU: Tách biệt SharedPreferences cho "Remember Me" và "User Session"
    private SharedPreferences loginPreferences; // Dành cho việc ghi nhớ username/password
    private static final String LOGIN_PREFS_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    private SharedPreferences userSessionPreferences; // Dành cho việc lưu session người dùng
    private static final String USER_PREFS_NAME = "UserPrefs";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(this);
        loginPreferences = getSharedPreferences(LOGIN_PREFS_NAME, Context.MODE_PRIVATE);
        userSessionPreferences = getSharedPreferences(USER_PREFS_NAME, Context.MODE_PRIVATE);

        initViews();
        loadSavedCredentials();
        setupClickListeners();
    }

    private void initViews() {
        edtUsername = findViewById(R.id.edt_login_username);
        edtPassword = findViewById(R.id.edt_login_password);
        btnLogin = findViewById(R.id.btn_login);
        txtForgotPassword = findViewById(R.id.txt_login_forgot_password);
        txtSignup = findViewById(R.id.txt_signup);
        checkBoxRemember = findViewById(R.id.checkBox);
    }

    private void setupClickListeners() {
        btnLogin.setOnClickListener(v -> performLogin());
        txtSignup.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
        txtForgotPassword.setOnClickListener(v -> Toast.makeText(this, "Tính năng sắp ra mắt!", Toast.LENGTH_SHORT).show());
    }

    private void loadSavedCredentials() {
        if (loginPreferences.getBoolean(KEY_REMEMBER, false)) {
            edtUsername.setText(loginPreferences.getString(KEY_USERNAME, ""));
            edtPassword.setText(loginPreferences.getString(KEY_PASSWORD, ""));
            checkBoxRemember.setChecked(true);
        }
    }

    private void saveCredentials(String username, String password, boolean remember) {
        SharedPreferences.Editor editor = loginPreferences.edit();
        if (remember) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            editor.clear(); // Xóa hết thông tin nếu không chọn "Remember Me"
        }
        editor.apply();
    }

    // BƯỚC QUAN TRỌNG: Lưu session của người dùng
    private void saveUserSession(int userId) {
        SharedPreferences.Editor editor = userSessionPreferences.edit();
        editor.putInt(KEY_USER_ID, userId);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    private void performLogin() {
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show();
            return;
        }

        // BƯỚC 1: Thay đổi cách gọi DB
        int userId = dbHelper.getUserIdByCredentials(username, password);

        if (userId != -1) {
            // Đăng nhập thành công
            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

            // Lưu thông tin cho "Remember Me"
            saveCredentials(username, password, checkBoxRemember.isChecked());

            // BƯỚC 2 & 3: Lưu session người dùng
            saveUserSession(userId);

            // Chuyển sang màn hình chính
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        } else {
            // Đăng nhập thất bại
            Toast.makeText(this, "Sai tên đăng nhập hoặc mật khẩu.", Toast.LENGTH_LONG).show();
            edtPassword.setText(""); // Xóa mật khẩu đã nhập sai
            edtPassword.requestFocus();
        }
    }
}