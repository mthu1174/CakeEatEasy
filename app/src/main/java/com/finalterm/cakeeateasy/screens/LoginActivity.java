package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    // UI Components
    private TextInputEditText edtUsername, edtPassword;
    private Button btnLogin, btnContinueGoogle, btnContinueFacebook;
    private TextView txtForgotPassword, txtSignup;
    private CheckBox checkBoxRemember;
    
    // Database helper
    private DatabaseHelper databaseHelper;
    
    // SharedPreferences for remembering credentials
    private SharedPreferences sharedPreferences;
    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_REMEMBER = "remember";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        
        // Setup window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);
        
        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        
        // Initialize views
        initViews();
        
        // Setup click listeners
        setupClickListeners();
        
        // Load saved credentials if remember is checked
        loadSavedCredentials();
    }

    /**
     * Initialize all UI components
     */
    private void initViews() {
        edtUsername = findViewById(R.id.edt_login_username);
        edtPassword = findViewById(R.id.edt_login_password);
        btnLogin = findViewById(R.id.btn_login);
        btnContinueGoogle = findViewById(R.id.btn_continue_google);
        btnContinueFacebook = findViewById(R.id.btn_continue_facebook);
        txtForgotPassword = findViewById(R.id.txt_login_forgot_password);
        txtSignup = findViewById(R.id.txt_signup);
        checkBoxRemember = findViewById(R.id.checkBox);
    }

    /**
     * Setup click listeners for all interactive elements
     */
    private void setupClickListeners() {
        // Login button click listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });

        // Sign up text click listener
        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSignUpActivity();
            }
        });

        // Forgot password click listener
        txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement forgot password functionality
                Toast.makeText(LoginActivity.this, "Forgot password feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        // Google login button click listener
        btnContinueGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Google login
                Toast.makeText(LoginActivity.this, "Google login feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        // Facebook login button click listener
        btnContinueFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement Facebook login
                Toast.makeText(LoginActivity.this, "Facebook login feature coming soon!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Load saved credentials if remember checkbox was previously checked
     */
    private void loadSavedCredentials() {
        boolean rememberChecked = sharedPreferences.getBoolean(KEY_REMEMBER, false);
        
        if (rememberChecked) {
            String savedUsername = sharedPreferences.getString(KEY_USERNAME, "");
            String savedPassword = sharedPreferences.getString(KEY_PASSWORD, "");
            
            if (!TextUtils.isEmpty(savedUsername)) {
                edtUsername.setText(savedUsername);
            }
            if (!TextUtils.isEmpty(savedPassword)) {
                edtPassword.setText(savedPassword);
            }
            
            // Set checkbox to checked state
            checkBoxRemember.setChecked(true);
        }
    }

    /**
     * Save credentials to SharedPreferences if remember checkbox is checked
     */
    private void saveCredentials(String username, String password, boolean remember) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        
        if (remember) {
            editor.putString(KEY_USERNAME, username);
            editor.putString(KEY_PASSWORD, password);
            editor.putBoolean(KEY_REMEMBER, true);
        } else {
            // Clear saved credentials if remember is unchecked
            editor.remove(KEY_USERNAME);
            editor.remove(KEY_PASSWORD);
            editor.putBoolean(KEY_REMEMBER, false);
        }
        
        editor.apply();
    }

    /**
     * Perform login validation and authentication
     */
    private void performLogin() {
        // Get input values
        String username = edtUsername.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean rememberChecked = checkBoxRemember.isChecked();

        // Validate input
        if (TextUtils.isEmpty(username)) {
            edtUsername.setError("Username is required");
            edtUsername.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            edtPassword.setError("Password is required");
            edtPassword.requestFocus();
            return;
        }

        // Check credentials in database
        boolean isValidCredentials = databaseHelper.checkUserCredentials(username, password);

        if (isValidCredentials) {
            // Save credentials if remember checkbox is checked
            saveCredentials(username, password, rememberChecked);
            
            // Login successful
            Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show();
            openMainActivity();
        } else {
            // Login failed
            Toast.makeText(this, "Wrong username or password. Please try again.", Toast.LENGTH_LONG).show();
            // Clear password field for retry
            edtPassword.setText("");
            edtPassword.requestFocus();
        }
    }

    /**
     * Open MainActivity
     */
    private void openMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Close login activity so user can't go back
    }

    /**
     * Open SignUpActivity
     */
    private void openSignUpActivity() {
        Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The DatabaseHelper now manages its own connection lifecycle.
        // No need to close it manually.
    }
}