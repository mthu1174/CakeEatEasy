package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.ProductGridAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Product;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class ProductListActivity extends AppCompatActivity implements ProductGridAdapter.OnProductClickListener {

    // --- UI Elements ---
    private RecyclerView rvProducts;
    private MaterialToolbar toolbar;
    private TextView txtEmptyMessage;

    // --- Data & Logic ---
    private ProductGridAdapter adapter;
    private List<Product> productList = new ArrayList<>();
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);

        dbHelper = new DatabaseHelper(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProductsFromDb();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        rvProducts = findViewById(R.id.rv_products);
        txtEmptyMessage = findViewById(R.id.txt_empty_message); // Tìm ID mới từ XML
    }

    private void setupToolbar() {
        toolbar.setTitle("Tất cả sản phẩm");
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void loadProductsFromDb() {
        List<Product> productsFromDb = dbHelper.getAllProducts();

        this.productList.clear();
        if (productsFromDb != null) {
            this.productList.addAll(productsFromDb);
        }

        // Cập nhật adapter nếu nó đã được tạo
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        checkEmptyState();
    }

    private void setupRecyclerView() {
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ProductGridAdapter(this, productList, this);
        rvProducts.setAdapter(adapter);
    }

    private void checkEmptyState() {
        // Luôn kiểm tra null trước khi sử dụng để tránh crash
        if (rvProducts == null || txtEmptyMessage == null) {
            return;
        }

        if (productList.isEmpty()) {
            rvProducts.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            rvProducts.setVisibility(View.VISIBLE);
            txtEmptyMessage.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }
}