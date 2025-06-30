package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.SelectableVoucherAdapter;
import com.finalterm.cakeeateasy.models.SelectableVoucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherActivity extends AppCompatActivity { // Hoặc BaseActivity nếu cần

    RecyclerView rvAvailable, rvUnavailable;
    SelectableVoucherAdapter availableAdapter, unavailableAdapter;
    List<SelectableVoucher> availableList, unavailableList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        initViews();
        loadData();
        setupRecyclerViews();

        // Handle voucher selection
        availableAdapter.setOnVoucherClickListener(voucher -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("voucher_amount", voucher.getDiscountAmount());
            resultIntent.putExtra("voucher_code", voucher.getVoucherCode());
            resultIntent.putExtra("voucher_type", voucher.getVoucherCode());
            setResult(RESULT_OK, resultIntent);
            finish();
        });

        unavailableAdapter.setOnVoucherClickListener(voucher -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("voucher_amount", voucher.getDiscountAmount());
            resultIntent.putExtra("voucher_code", voucher.getVoucherCode());
            resultIntent.putExtra("voucher_type", voucher.getVoucherCode());
            setResult(RESULT_OK, resultIntent);
            finish();
        });
    }

    private void initViews() {
        rvAvailable = findViewById(R.id.rv_available_vouchers);
        rvUnavailable = findViewById(R.id.rv_unavailable_vouchers);
    }

    private void loadData() {
        // Lấy dữ liệu từ database và phân loại vào 2 danh sách
        availableList = new ArrayList<>();
        unavailableList = new ArrayList<>();

        // Dữ liệu giả
        availableList.add(new SelectableVoucher("Save 10% on orders over 1000k", "Valid until 01/08/2026", R.drawable.ic_cake_pink, true, "SAVE10", 100000));
        availableList.add(new SelectableVoucher("Save 5% on orders over 800k", "Valid until 01/08/2026", R.drawable.ic_cake_pink, true, "SAVE5", 50000));
        availableList.add(new SelectableVoucher("Freeship on orders over 500k", "Valid until 01/08/2026", R.drawable.ic_ship, true, "FREESHIP", 30000));

        unavailableList.add(new SelectableVoucher("Save 15% on orders over 1500k", "Valid until 01/08/2026", R.drawable.ic_cake_grey, false, "SAVE15", 150000));
        unavailableList.add(new SelectableVoucher("Save 20% on orders over 2000k", "Valid until 01/08/2026", R.drawable.ic_cake_grey, false, "SAVE20", 200000));
    }

    private void setupRecyclerViews() {
        // Setup cho voucher có thể sử dụng
        rvAvailable.setLayoutManager(new LinearLayoutManager(this));
        availableAdapter = new SelectableVoucherAdapter(this, availableList);
        rvAvailable.setAdapter(availableAdapter);

        // Setup cho voucher không thể sử dụng
        rvUnavailable.setLayoutManager(new LinearLayoutManager(this));
        unavailableAdapter = new SelectableVoucherAdapter(this, unavailableList);
        rvUnavailable.setAdapter(unavailableAdapter);
    }
}