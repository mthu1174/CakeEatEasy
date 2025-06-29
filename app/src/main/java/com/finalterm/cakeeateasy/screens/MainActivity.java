package com.finalterm.cakeeateasy.screens; // Giữ nguyên package của bạn

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

// Import các lớp Model và Adapter mà bạn đã tạo


import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.CategoryAdapter;
import com.finalterm.cakeeateasy.adapters.PromoProductAdapter;
import com.finalterm.cakeeateasy.adapters.VoucherGridAdapter;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.models.Voucher;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends com.finalterm.cakeeateasy.screens.BaseActivity {

    // Khai báo tất cả các RecyclerView và Adapter
    RecyclerView rvCollections, rvPromo, rvVouchers;
    CategoryAdapter categoryAdapter;
    PromoProductAdapter promoAdapter;
    VoucherGridAdapter voucherAdapter;

    // Khai báo các danh sách dữ liệu
    List<Category> categoryList;
    List<Product> promoProductList;
    List<Voucher> voucherList;

    @Override
    public int getNavItemIndex() {
        // Trả về 0 vì đây là item "Home"
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_main);

        // 1. Khởi tạo các view (findViewById)
        initViews();

        // 2. Tải dữ liệu (từ database hoặc tạo dữ liệu giả để test)
        loadData();

        // 3. Thiết lập các Adapter và RecyclerView để hiển thị dữ liệu
        setupRecyclerViews();
    }

    /**
     * Hàm này để gom việc tìm kiếm view vào một chỗ cho gọn gàng.
     */
    private void initViews() {
        rvCollections = findViewById(R.id.rv_collections);
        rvPromo = findViewById(R.id.rv_promo);
        rvVouchers = findViewById(R.id.rv_vouchers);
    }

    /**
     * Hàm này để tải tất cả dữ liệu cần thiết cho màn hình Home.
     * Trong thực tế, bạn sẽ gọi API hoặc query từ database ở đây.
     */
    private void loadData() {
        // Dữ liệu giả cho Collection
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Birthday", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Wedding", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Vanilla", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Fruit", R.drawable.placeholder_cake_promo));

        // Dữ liệu giả cho Today Promo
        promoProductList = new ArrayList<>();
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Karl", "Vani & Yogurt", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));

        // Dữ liệu giả cho Hot Voucher
        voucherList = new ArrayList<>();
        voucherList.add(new Voucher("New Member", "10% off", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_gift));
        voucherList.add(new Voucher("Freeshipping", "Order 500k+", R.color.voucher_green, R.color.voucher_icon_bg_green, R.drawable.ic_shopping_cart));
        voucherList.add(new Voucher("Happy Valentine", "10% off", R.color.home_pink, R.color.voucher_icon_bg_pink, R.drawable.ic_favorite_doutone));
        voucherList.add(new Voucher("Holiday day!", "Buy 1 get 1", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_sun));
    }

    /**
     * Hàm này chịu trách nhiệm thiết lập LayoutManager và Adapter cho tất cả RecyclerView.
     */
    private void setupRecyclerViews() {
        // --- Setup Collection RecyclerView ---
        rvCollections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this, categoryList);
        rvCollections.setAdapter(categoryAdapter);

        // --- Setup Today Promo RecyclerView ---
        rvPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        promoAdapter = new PromoProductAdapter(this, promoProductList);
        rvPromo.setAdapter(promoAdapter);

        // --- Setup Hot Voucher RecyclerView ---
        rvVouchers.setLayoutManager(new GridLayoutManager(this, 2)); // Dạng lưới 2 cột
        voucherAdapter = new VoucherGridAdapter(this, voucherList);
        rvVouchers.setAdapter(voucherAdapter);
    }
}