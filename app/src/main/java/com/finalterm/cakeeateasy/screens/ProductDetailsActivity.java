package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.ReviewAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.CartItem;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.models.Review;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_PRODUCT_ID = "product_id";
    private static final String TAG = "ProductDetails";

    // --- UI Elements ---
    private ImageView imgProductMain, btnFavorite, btnBack, btnIncrease, btnDecrease,btnCart;
    private TextView txtProductNameTitle, txtCategory, txtProductName, txtDescription,
            txtCurrentPrice, txtOldPrice, txtDiscount, txtQuantity, cartBadge;
    private Button btnAddToCart, btnBuyNow;
    private RecyclerView rvReviews; // RecyclerView cho reviews

    // --- Data & Logic ---
    private DatabaseHelper dbHelper;
    private Product currentProduct;
    private int productId = -1;
    private int currentUserId = -1;
    private int quantity = 1;
    private boolean isFavourite = false;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        dbHelper = new DatabaseHelper(this);

        // Lấy ID sản phẩm và ID người dùng
        if (getIntent().hasExtra(EXTRA_PRODUCT_ID)) {
            productId = getIntent().getIntExtra(EXTRA_PRODUCT_ID, -1);
        }
        loadCurrentUserId();

        if (productId == -1) {
            Toast.makeText(this, "Lỗi: không tìm thấy sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupReviewSection(); // Cài đặt RecyclerView trước khi tải dữ liệu
        loadProductData();
        setupClickListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateCartBadge(); // Cập nhật số lượng trong giỏ hàng mỗi khi quay lại
    }

    private void initViews() {
        // Header
        btnBack = findViewById(R.id.btn_back);
        txtProductNameTitle = findViewById(R.id.txt_product_name_title);
        cartBadge = findViewById(R.id.cart_badge);
        btnCart = findViewById(R.id.btn_cart);

        // Product Info
        imgProductMain = findViewById(R.id.img_product_main);
        btnFavorite = findViewById(R.id.btn_favorite);

        // === SỬA LẠI CÁC ID Ở ĐÂY CHO KHỚP ===
        txtCategory = findViewById(R.id.txt_product_category);
        txtProductName = findViewById(R.id.txt_product_name);
        txtDescription = findViewById(R.id.txt_product_description);
        txtCurrentPrice = findViewById(R.id.txt_product_current_price);
        txtOldPrice = findViewById(R.id.txt_product_old_price);
        txtDiscount = findViewById(R.id.txt_discount_percent);

        // Quantity & Actions
        btnDecrease = findViewById(R.id.btn_decrease);
        txtQuantity = findViewById(R.id.txt_quantity_picker);
        btnIncrease = findViewById(R.id.btn_increase);
        btnAddToCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        // Review Section
        rvReviews = findViewById(R.id.rv_product_reviews);
    }

    private void loadCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);
    }

    private void loadProductData() {
        currentProduct = dbHelper.getProductById(productId);
        if (currentProduct != null) {
            displayProductInfo();
            loadReviews();
            checkFavouriteStatus();
        } else {
            Toast.makeText(this, "Không thể tải chi tiết sản phẩm.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void displayProductInfo() {
        txtProductNameTitle.setText(currentProduct.getName());
        txtProductName.setText(currentProduct.getName());
        txtCategory.setText(currentProduct.getCategoryName());
        txtDescription.setText(currentProduct.getDescription());

        Glide.with(this)
                .load(currentProduct.getImageUrl())
                .placeholder(R.drawable.placeholder_cake_promo)
                .into(imgProductMain);

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        txtCurrentPrice.setText(currencyFormat.format(currentProduct.getPrice()));

        if (currentProduct.getOriginalPrice() > 0 && currentProduct.getOriginalPrice() > currentProduct.getPrice()) {
            txtOldPrice.setVisibility(View.VISIBLE);
            txtOldPrice.setText(currencyFormat.format(currentProduct.getOriginalPrice()));
            txtOldPrice.setPaintFlags(txtOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            double discountPercent = ((currentProduct.getOriginalPrice() - currentProduct.getPrice()) / currentProduct.getOriginalPrice()) * 100;
            txtDiscount.setVisibility(View.VISIBLE);
            txtDiscount.setText(String.format(Locale.US, "%.0f%% OFF", discountPercent));
        } else {
            txtOldPrice.setVisibility(View.GONE);
            txtDiscount.setVisibility(View.GONE);
        }
    }

    private void setupReviewSection() {
        reviewAdapter = new ReviewAdapter(this, reviewList);
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
        rvReviews.setAdapter(reviewAdapter);
        rvReviews.setNestedScrollingEnabled(false);
    }

    private void loadReviews() {
        List<Review> loadedReviews = dbHelper.getReviewsByProductId(productId);
        reviewAdapter.updateReviews(loadedReviews);
    }

    // Trong file ProductDetailsActivity.java

    private void setupClickListeners() {
        // Các listener khác giữ nguyên
        btnBack.setOnClickListener(v -> finish());
        findViewById(R.id.cart_container).setOnClickListener(v ->
                startActivity(new Intent(ProductDetailsActivity.this, CartActivity.class)));
        btnFavorite.setOnClickListener(v -> toggleFavourite());
        btnIncrease.setOnClickListener(v -> updateQuantity(1));
        btnDecrease.setOnClickListener(v -> updateQuantity(-1));

        // Nút "Add to Cart": Chỉ thêm vào giỏ hàng
        btnAddToCart.setOnClickListener(v -> {
            addToCart();
        });

        // === SỬA LẠI LOGIC NÚT "BUY NOW" TẠI ĐÂY ===
        btnBuyNow.setOnClickListener(v -> {
            // Trước tiên, vẫn thêm sản phẩm vào giỏ hàng trong CSDL
            boolean addedSuccessfully = addToCart();

            // Nếu thêm thành công (tức là người dùng đã đăng nhập)
            if (addedSuccessfully) {
                // Sau khi đã thêm vào CSDL, lấy lại toàn bộ giỏ hàng
                // để đảm bảo số lượng sản phẩm là mới nhất.
                // Điều này đảm bảo CheckoutActivity nhận được tất cả sản phẩm
                // chứ không chỉ sản phẩm vừa thêm.
                List<CartItem> allCartItems = dbHelper.getCartItemsByCustomerId(currentUserId);

                // Nếu giỏ hàng không rỗng, mới chuyển màn hình
                if (allCartItems != null && !allCartItems.isEmpty()) {
                    Intent intent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);

                    // Gửi toàn bộ giỏ hàng đi với key là "selected_items"
                    // giống hệt như cách CartActivity gửi đi.
                    intent.putExtra("selected_items", (Serializable) allCartItems);

                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Lỗi: Không tìm thấy sản phẩm trong giỏ hàng để thanh toán.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Hàm addToCart vẫn phải trả về boolean
    private boolean addToCart() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để thêm vào giỏ hàng.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (currentProduct == null) {
            Toast.makeText(this, "Lỗi sản phẩm.", Toast.LENGTH_SHORT).show();
            return false;
        }
        dbHelper.addToCart(currentUserId, productId, quantity, currentProduct.getPrice());
        Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        updateCartBadge();
        return true;
    }

    private void updateQuantity(int change) {
        quantity += change;
        if (quantity < 1) {
            quantity = 1;
        }
        txtQuantity.setText(String.valueOf(quantity));
    }

    private void toggleFavourite() {
        if (currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để sử dụng tính năng này.", Toast.LENGTH_SHORT).show();
            return;
        }
        isFavourite = !isFavourite;
        if (isFavourite) {
            dbHelper.addToWishlist(currentUserId, productId);
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.removeFromWishlist(currentUserId, productId);
            Toast.makeText(this, "Đã xóa khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
        }
        updateFavouriteIcon();
    }

    private void checkFavouriteStatus() {
        if (currentUserId != -1) {
            isFavourite = dbHelper.isFavourite(currentUserId, productId);
            updateFavouriteIcon();
        }
    }

    private void updateFavouriteIcon() {
        if (isFavourite) {
            btnFavorite.setImageResource(R.drawable.ic_favorite_filled);
            btnFavorite.setColorFilter(ContextCompat.getColor(this, R.color.status_error_main));
        } else {
            btnFavorite.setImageResource(R.drawable.ic_favorite_outline);
            btnFavorite.clearColorFilter();
        }
    }

    // Trong ProductDetailsActivity.java



    private void updateCartBadge() {
        if (currentUserId != -1) {
            int cartItemCount = dbHelper.getCartItemCount(currentUserId);
            if (cartItemCount > 0) {
                cartBadge.setVisibility(View.VISIBLE);
                cartBadge.setText(String.valueOf(cartItemCount));
            } else {
                cartBadge.setVisibility(View.GONE);
            }
        }
    }

}