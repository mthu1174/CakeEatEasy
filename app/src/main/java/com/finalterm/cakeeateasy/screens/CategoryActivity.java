package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

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

// GIỮ LẠI: Implement interface để lắng nghe sự kiện click từ Adapter
public class CategoryActivity extends AppCompatActivity implements ProductGridAdapter.OnProductClickListener {

    // GIỮ LẠI: Dùng cả ID và Name để truyền dữ liệu, ID để query, Name để hiển thị
    public static final String EXTRA_CATEGORY_ID = "extra_category_id";
    public static final String EXTRA_CATEGORY_NAME = "extra_category_name";

    // --- UI Elements ---
    private MaterialToolbar toolbar;
    private RecyclerView rvProducts;
    private ProductGridAdapter adapter;

    // --- Data & Logic ---
    private List<Product> productList;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Lấy dữ liệu từ Intent
        int categoryId = getIntent().getIntExtra(EXTRA_CATEGORY_ID, -1);
        String categoryName = getIntent().getStringExtra(EXTRA_CATEGORY_NAME);

        // Kiểm tra dữ liệu đầu vào. Nếu không hợp lệ, đóng Activity.
        if (categoryId == -1) {
            Toast.makeText(this, "Danh mục không hợp lệ!", Toast.LENGTH_SHORT).show();
            finish(); // Đóng Activity ngay lập tức
            return; // Ngăn không cho code bên dưới chạy
        }

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        initViews();
        setupToolbar(categoryName);
        loadProductsFromDb(categoryId); // Lấy dữ liệu từ DB
        setupRecyclerView();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar_category);
        rvProducts = findViewById(R.id.rv_products);
    }

    private void setupToolbar(String title) {
        // Nếu title không được truyền qua, hiển thị một tiêu đề mặc định
        toolbar.setTitle(title != null ? title : "Sản phẩm");
        // Thiết lập sự kiện click cho nút back trên toolbar
        toolbar.setNavigationOnClickListener(v -> onBackPressed()); // Hoặc finish()
    }

    /**
     * Tải danh sách sản phẩm từ cơ sở dữ liệu dựa trên categoryId.
     * @param categoryId ID của danh mục cần tải sản phẩm.
     */
    private void loadProductsFromDb(int categoryId) {
        // TỐI ƯU: Đảm bảo productList luôn được khởi tạo trước khi sử dụng.
        // Luôn lấy dữ liệu mới nhất từ DB.
        productList = dbHelper.getProductsByCategoryId(categoryId);

        // Nếu danh sách rỗng, có thể hiển thị một thông báo cho người dùng (tùy chọn)
        if (productList.isEmpty()) {
            Toast.makeText(this, "Hiện chưa có sản phẩm nào trong danh mục này.", Toast.LENGTH_LONG).show();
        }
    }

    private void setupRecyclerView() {
        // TỐI ƯU: Khởi tạo list rỗng nếu nó null để tránh NullPointerException.
        if (productList == null) {
            productList = new ArrayList<>();
        }

        // Thiết lập layout manager cho RecyclerView (dạng lưới 2 cột)
        rvProducts.setLayoutManager(new GridLayoutManager(this, 2));

        // Khởi tạo Adapter và truyền listener (this) vào.
        adapter = new ProductGridAdapter(this, productList, this);

        // Gán adapter cho RecyclerView
        rvProducts.setAdapter(adapter);
    }

    /**
     * Xử lý sự kiện khi người dùng click vào một sản phẩm trong lưới.
     * Hàm này được gọi từ ProductGridAdapter vì Activity này đã implement OnProductClickListener.
     * @param product Đối tượng Product đã được click.
     */
    @Override
    public void onProductClick(Product product) {
        // Mở màn hình Chi tiết sản phẩm và truyền ID của sản phẩm đó đi
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }
}