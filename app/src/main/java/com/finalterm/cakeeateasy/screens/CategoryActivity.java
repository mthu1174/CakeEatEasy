
package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.appbar.MaterialToolbar;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.ProductGridAdapter;
import com.finalterm.cakeeateasy.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CategoryActivity extends AppCompatActivity {

    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    private MaterialToolbar toolbar;
    private RecyclerView rvProducts;
    private ProductGridAdapter adapter;
    private List<Product> productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Lấy tên category từ Intent
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        initViews();
        setupToolbar(categoryName);
        loadProductsForCategory(categoryName);
        setupRecyclerView();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_category);
        rvProducts = findViewById(R.id.rv_products);
    }

    private void setupToolbar(String title) {
        // Đặt tiêu đề cho Toolbar
        toolbar.setTitle(title);
        // Thiết lập sự kiện click cho nút back
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void loadProductsForCategory(String categoryName) {
        productList = new ArrayList<>();
        // Đây là phần giả lập dữ liệu. Trong thực tế, bạn sẽ query từ database hoặc API
        // dựa trên categoryName.
        if (Objects.equals(categoryName, "Vanilla")) {
            productList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
            productList.add(new Product("Karl", "Vani & Yogurt", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
            productList.add(new Product("Ever Muse", "Vani & Blueberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        } else if (Objects.equals(categoryName, "Wedding")) {
            productList.add(new Product("White Swan", "Wedding Cake", "1.200.000 đ", "", "", R.drawable.placeholder_cake_promo));
        } else {
            // Thêm các sản phẩm mặc định hoặc cho các category khác
            productList.add(new Product("Chloe", "Vani & Chocolate", "600.000 đ", "", "", R.drawable.placeholder_cake_promo));
            productList.add(new Product("Matcha Jasmine", "Vani & Matcha", "500.000 đ", "", "", R.drawable.placeholder_cake_promo));
            productList.add(new Product("Jolie", "Vani & Cherry", "630.000 đ", "700.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        }
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductGridAdapter(this, productList);
        rvProducts.setAdapter(adapter);
    }
}