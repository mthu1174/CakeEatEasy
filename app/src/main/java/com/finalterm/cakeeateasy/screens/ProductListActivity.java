package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.ProductGridAdapter;
import com.finalterm.cakeeateasy.models.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity {

    RecyclerView rvProducts;
    ProductGridAdapter adapter;
    List<Product> productList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        // Xử lý nút back trên toolbar
//        findViewById(R.id.toolbar).setOnClickListener(v -> finish()); // Đóng Activity hiện tại

        rvProducts = findViewById(R.id.rv_products);
        // Đặt LayoutManager trong code (cách làm chuẩn)
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Tải dữ liệu và thiết lập adapter
        loadProducts();
        adapter = new ProductGridAdapter(this, productList); // Bạn sẽ cần tạo Adapter này
        rvProducts.setAdapter(adapter);
    }

    private void loadProducts() {
        productList = new ArrayList<>();
        // TODO: Lấy dữ liệu từ database hoặc API dựa trên danh mục được chọn
        // Dữ liệu giả:
        productList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        productList.add(new Product("Karl", "Vani & Yogurt", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        productList.add(new Product("Chloe", "Vani & Chocolate", "600.000 đ", null, null, R.drawable.placeholder_cake_promo));
        productList.add(new Product("Matcha Jasmine", "Vani & Matcha", "500.000 đ", null, null, R.drawable.placeholder_cake_promo));
        productList.add(new Product("Jolie", "Vani & Cherry", "630.000 đ", "700.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        productList.add(new Product("Ever Muse", "Vani & Blueberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
    }
}