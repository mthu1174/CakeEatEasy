package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Customer;
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class SignUpActivity extends AppCompatActivity {

    // UI Components
    private ImageView imgBack;
    private TextInputEditText edtFullName, edtUsername, edtEmail, edtDateOfBirth, edtPhoneNumber, edtPassword, edtConfirmPassword;
    private Button btnSignup;
    private TextView txtLogin;
    
    // Database helper
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);
        
        // Setup window insets - using the ScrollView as the root view
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize views
        initViews();
        
        // Setup click listeners
        setupClickListeners();
    }

    /**
     * Initialize all UI components
     */
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
        txtLogin = findViewById(R.id.txt_signup); // This is the "Login" text in signup screen
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Back button click listener
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to previous activity
            }
        });

        // Sign up button click listener
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performSignUp();
            }
        });

        // Login text click listener (to go back to login)
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Go back to login activity
            }
        });
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Validate phone number format (basic validation)
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        return !TextUtils.isEmpty(phoneNumber) && phoneNumber.length() >= 10;
    }

    /**
     * Validate password strength
     */
    private boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= 6;
    }

    /**
     * Perform sign up validation and registration
     */
    private void performSignUp() {
        // Get input values
        String fullName = edtFullName.getText().toString().trim();
        String username = edtUsername.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String dateOfBirth = edtDateOfBirth.getText().toString().trim();
        String phoneNumber = edtPhoneNumber.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        // Validate all fields
        if (TextUtils.isEmpty(fullName)) {
            edtFullName.setError("Full name is required");
            edtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username is required");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return;
        }

        if (!isValidEmail(email)) {
            edtEmail.setError("Please enter a valid email address");
            edtEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(dateOfBirth)) {
            edtDateOfBirth.setError("Date of birth is required");
            edtDateOfBirth.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phoneNumber)) {
            edtPhoneNumber.setError("Phone number is required");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (!isValidPhoneNumber(phoneNumber)) {
            edtPhoneNumber.setError("Please enter a valid phone number");
            edtPhoneNumber.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }

        if (!isValidPassword(password)) {
            edtPassword.setError("Password must be at least 6 characters long");
            edtPassword.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            edtConfirmPassword.setError("Please confirm your password");
            edtConfirmPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            edtConfirmPassword.setError("Passwords do not match");
            edtConfirmPassword.requestFocus();
            return;
        }

        // Check if username already exists
        if (databaseHelper.isUsernameExists(username)) {
            edtUsername.setError("Username already exists. Please choose a different one.");
            edtUsername.requestFocus();
            return;
        }

        // Create customer object
        Customer customer = new Customer(username, password, email, phoneNumber, dateOfBirth);

        // Save customer to database
        long result = databaseHelper.addCustomer(customer);

        if (result != -1) {
            // Registration successful
            Toast.makeText(this, "Registration successful! You can now login.", Toast.LENGTH_LONG).show();
            
            // Clear all fields
            clearAllFields();
            
            // Go back to login activity
            finish();
        } else {
            // Registration failed
            Toast.makeText(this, "Registration failed. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Clear all input fields
     */
    private void clearAllFields() {
        edtFullName.setText("");
        edtUsername.setText("");
        edtEmail.setText("");
        edtDateOfBirth.setText("");
        edtPhoneNumber.setText("");
        edtPassword.setText("");
        edtConfirmPassword.setText("");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The DatabaseHelper now manages its own connection lifecycle.
        // No need to close it manually.
    }
}