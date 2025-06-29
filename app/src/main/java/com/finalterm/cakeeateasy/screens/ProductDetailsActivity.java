package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.connectors.ProductDetailsConnector;
import com.finalterm.cakeeateasy.models.ProductDetails;

public class ProductDetailsActivity extends AppCompatActivity {
    private EditText edtProductNameTitle, edtProductCategory, edtProductName, edtProductDescription,
            edtProductCurrentPrice, edtProductOldPrice, edtDiscountPercent, edtQuantityPicker,
            edtSizeContent, edtUsageInstruction, edtIncludedAccessories,
            edtTitleReview, edtReviewContent, edtCustomerName,
            edtTitleReview2, edtReviewContent2, edtCustomerName2;
    private ImageView btnDecrease, btnIncrease, btnBack, btnCart, btnFavorite, imgChatWithShop;
    private Button btnAddCart, btnBuyNow;
    private int quantity = 1;
    private int productId = 1; // Default, can be replaced by intent extra
    private static int cartCount = 0;
    private Toast cartToast;
    private TextView cartBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_product_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Always show product with id 1
        productId = 1;
        Toast.makeText(this, "Showing product with ID 1", Toast.LENGTH_SHORT).show();

        // Bind views
        edtProductNameTitle = findViewById(R.id.edt_product_name_title);
        edtProductCategory = findViewById(R.id.edt_product_category);
        edtProductName = findViewById(R.id.edt_product_name);
        edtProductDescription = findViewById(R.id.edt_product_description);
        edtProductCurrentPrice = findViewById(R.id.edt_product_current_price);
        edtProductOldPrice = findViewById(R.id.edt_product_old_price);
        edtDiscountPercent = findViewById(R.id.edt_discount_percent);
        edtQuantityPicker = findViewById(R.id.edt_quantity_picker);
        edtSizeContent = findViewById(R.id.edt_size_content);
        edtUsageInstruction = findViewById(R.id.edt_usage_instruction);
        edtIncludedAccessories = findViewById(R.id.edt_included_accessories);
        edtTitleReview = findViewById(R.id.edt_title_review);
        edtReviewContent = findViewById(R.id.edt_review_content);
        edtCustomerName = findViewById(R.id.edt_customer_name);
        edtTitleReview2 = findViewById(R.id.edt_title_review_2);
        edtReviewContent2 = findViewById(R.id.edt_review_content_2);
        edtCustomerName2 = findViewById(R.id.edt_customer_name_2);

        btnDecrease = findViewById(R.id.btn_decrease);
        btnIncrease = findViewById(R.id.btn_increase);
        btnBack = findViewById(R.id.btn_back);
        btnCart = findViewById(R.id.btn_cart);
        btnFavorite = findViewById(R.id.btn_favorite);
        imgChatWithShop = findViewById(R.id.img_chat_with_shop);
        btnAddCart = findViewById(R.id.btn_add_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);

        cartBadge = findViewById(R.id.cart_badge);
        updateCartBadge();

        // Load product details from DB
        ProductDetailsConnector db = new ProductDetailsConnector(this);
        ProductDetails product = db.getProductDetailsById(productId);
        if (product != null) {
            edtProductNameTitle.setText(product.getProductNameTitle());
            edtProductCategory.setText(product.getProductCategory());
            edtProductName.setText(product.getProductName());
            edtProductDescription.setText(product.getProductDescription());
            edtProductCurrentPrice.setText(String.format("%.0f đ", product.getProductCurrentPrice()));
            edtProductOldPrice.setText(String.format("%.0f đ", product.getProductOldPrice()));
            edtDiscountPercent.setText(String.format("%.0f%% off", product.getDiscountPercent()));
            quantity = product.getQuantityPicker();
            edtQuantityPicker.setText(String.valueOf(quantity));
            edtSizeContent.setText(product.getSizeContent());
            edtUsageInstruction.setText(product.getUsageInstruction());
            edtIncludedAccessories.setText(product.getIncludedAccessories());
            edtTitleReview.setText(product.getTitleReview());
            edtReviewContent.setText(product.getReviewContent());
            edtCustomerName.setText(product.getCustomerName());
            edtTitleReview2.setText(product.getTitleReview2());
            edtReviewContent2.setText(product.getReviewContent2());
            edtCustomerName2.setText(product.getCustomerName2());
        } else {
            Toast.makeText(this, "Product not found", Toast.LENGTH_SHORT).show();
        }

        // Quantity picker logic
        btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                edtQuantityPicker.setText(String.valueOf(quantity));
            }
        });
        btnIncrease.setOnClickListener(v -> {
            quantity++;
            edtQuantityPicker.setText(String.valueOf(quantity));
        });

        // Back button
        btnBack.setOnClickListener(v -> finish());

        // Cart button: open CartActivity
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Favorite button (stub)
        btnFavorite.setOnClickListener(v -> Toast.makeText(this, "Favorite (not implemented)", Toast.LENGTH_SHORT).show());

        // Add to cart: increment cart count and show badge (Toast for demo)
        btnAddCart.setOnClickListener(v -> {
            cartCount += quantity;
            updateCartBadge();
            if (cartToast != null) cartToast.cancel();
            cartToast = Toast.makeText(this, "Cart: " + cartCount, Toast.LENGTH_SHORT);
            cartToast.show();
        });

        // Buy now: send all info to CheckoutActivity
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(ProductDetailsActivity.this, CheckoutActivity.class);
            intent.putExtra("ProductID", productId);
            intent.putExtra("ProductNameTitle", edtProductNameTitle.getText().toString());
            intent.putExtra("ProductCategory", edtProductCategory.getText().toString());
            intent.putExtra("ProductName", edtProductName.getText().toString());
            intent.putExtra("ProductDescription", edtProductDescription.getText().toString());
            intent.putExtra("ProductCurrentPrice", edtProductCurrentPrice.getText().toString());
            intent.putExtra("ProductOldPrice", edtProductOldPrice.getText().toString());
            intent.putExtra("DiscountPercent", edtDiscountPercent.getText().toString());
            intent.putExtra("Quantity", quantity);
            intent.putExtra("SizeContent", edtSizeContent.getText().toString());
            intent.putExtra("UsageInstruction", edtUsageInstruction.getText().toString());
            intent.putExtra("IncludedAccessories", edtIncludedAccessories.getText().toString());
            // Add more fields as needed
            startActivity(intent);
        });

        // Chat with shop (stub)
        imgChatWithShop.setOnClickListener(v -> Toast.makeText(this, "Chat with shop (not implemented)", Toast.LENGTH_SHORT).show());
    }

    private void updateCartBadge() {
        if (cartBadge != null) {
            if (cartCount > 0) {
                cartBadge.setText(String.valueOf(cartCount));
                cartBadge.setVisibility(View.VISIBLE);
            } else {
                cartBadge.setVisibility(View.GONE);
            }
        }
    }
}