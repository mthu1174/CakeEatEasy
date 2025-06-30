package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.connectors.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SuccessCheckoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_success_checkout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Insert a new order and get the invoice number
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        String customerName = "Guest"; // TODO: Replace with actual customer if available
        String orderDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());
        double totalAmount = 0; // TODO: Replace with actual total if available
        String status = "completed";
        long invoiceId = dbHelper.insertOrder(customerName, orderDate, totalAmount, status);

        // Display the invoice number
        TextView tvInvoice = findViewById(R.id.tv_invoice);
        tvInvoice.setText(String.format("#%06d", invoiceId));

        // Update the thank you message to include the invoice number
        TextView tvMessage = findViewById(R.id.txt_thank_you_invoice);
        tvMessage.setText("Your Order will be delivered with invoice #" + String.format("%06d", invoiceId) + ". You can track the delivery in the order section.");

        // Handle continue shopping button
        Button btnContinue = findViewById(R.id.btn_continue_shopping);
        btnContinue.setOnClickListener(v -> {
            Intent intent = new Intent(SuccessCheckoutActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}