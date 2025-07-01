package com.finalterm.cakeeateasy.screens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Customer;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {

    // --- UI Elements ---
    private ImageView imgBack;
    private TextInputEditText edtFullName, edtUsername, edtEmail, edtDateOfBirth, edtPhoneNumber, edtPassword, edtConfirmPassword;
    private Button btnSignup;
    private TextView txtLogin;

    // --- Data & Logic ---
    private DatabaseHelper dbHelper;
    private final Calendar myCalendar = Calendar.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        dbHelper = new DatabaseHelper(this);
        initViews();
        setupClickListeners();
    }

    private void initViews() {
        imgBack = findViewById(R.id.img_back);
        edtFullName = findViewById(R.id.edt_full_name);
        edtUsername = findViewById(R.id.edt_username);
        edtEmail = findViewById(R.id.edt_email);
        edtDateOfBirth = findViewById(R.id.edt_dateofbirth);
        edtPhoneNumber = findViewById(R.id.edt_phonenumber);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnSignup = findViewById(R.id.btn_signup);
        txtLogin = findViewById(R.id.txt_login_title1); // Giả sử ID của text "Login" là đây
    }

    private void setupClickListeners() {
        imgBack.setOnClickListener(v -> finish());
        btnSignup.setOnClickListener(v -> performSignUp());
        // Khi nhấn vào "Login", quay lại màn hình Login
        txtLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Cải tiến: không cho bàn phím hiện lên khi nhấn vào ô ngày sinh
        edtDateOfBirth.setFocusable(false);
        edtDateOfBirth.setClickable(true);
        edtDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
    }

    private void showDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDobLabel();
        };

        new DatePickerDialog(this, dateSetListener,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDobLabel() {
        // Định dạng ngày theo chuẩn "dd/MM/yyyy"
        String myFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDateOfBirth.setText(sdf.format(myCalendar.getTime()));
    }

    private void performSignUp() {
        // 1. Lấy và làm sạch dữ liệu
        String fullName = edtFullName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dateOfBirth = edtDateOfBirth.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String password = edtPassword.getText().toString(); // Không trim mật khẩu
        String confirmPassword = edtConfirmPassword.getText().toString();

        // 2. Validate dữ liệu
        if (!isInputValid(fullName, username, email, phoneNumber, password, confirmPassword)) {
            return; // Dừng lại nếu có lỗi
        }

        // 3. Nếu hợp lệ, tạo đối tượng Customer
        Customer newCustomer = new Customer();
        newCustomer.setName(fullName);
        newCustomer.setUsername(username);
        newCustomer.setEmail(email);
        newCustomer.setPassword(password); // TODO: Nên mã hóa mật khẩu trước khi lưu
        newCustomer.setPhone(phoneNumber);
        newCustomer.setDob(dateOfBirth);
        // newCustomer.setAddress(""); // Có thể set địa chỉ mặc định là rỗng

        // 4. Thêm vào database
        long result = dbHelper.addCustomer(newCustomer);

        if (result != -1) {
            Toast.makeText(this, "Đăng ký thành công! Vui lòng đăng nhập.", Toast.LENGTH_LONG).show();
            // Quay về màn hình Login sau khi thành công
            startActivity(new Intent(this, LoginActivity.class));
            finishAffinity(); // Đóng cả SignUp và các màn hình trước nó (nếu có)
        } else {
            Toast.makeText(this, "Đăng ký thất bại. Tên đăng nhập hoặc email có thể đã tồn tại.", Toast.LENGTH_LONG).show();
        }
    }

    private boolean isInputValid(String fullName, String username, String email, String phone, String password, String confirmPassword) {
        // Thứ tự kiểm tra: Rỗng -> Định dạng -> Tồn tại trong DB
        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Vui lòng điền đầy đủ các trường bắt buộc.", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Định dạng email không hợp lệ");
            edtEmail.requestFocus();
            return false;
        }

        if (password.length() < 6) {
            edtPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
            edtPassword.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            edtConfirmPassword.requestFocus();
            return false;
        }

        if (dbHelper.isUsernameExists(username)) {
            edtUsername.setError("Tên đăng nhập này đã được sử dụng");
            edtUsername.requestFocus();
            return false;
        }

        if (dbHelper.isEmailExists(email)) {
            edtEmail.setError("Email này đã được đăng ký");
            edtEmail.requestFocus();
            return false;
        }

        return true; // Tất cả đều hợp lệ
    }
}