package com.finalterm.cakeeateasy.screens;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.FavouriteAdapter; // Giả sử bạn có adapter này
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Product;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends BaseActivity implements FavouriteAdapter.OnFavouriteInteractionListener {

    private static final String TAG = "FavouriteActivity";

    // --- UI Elements ---
    private RecyclerView rvFavourites;
    private TextView txtEmptyMessage;
    private MaterialToolbar toolbar;

    // --- Data & Logic ---
    private FavouriteAdapter adapter;
    private List<Product> favouriteList = new ArrayList<>();
    private DatabaseHelper dbHelper;
    private int currentUserId = -1;

    @Override
    public int getNavItemIndex() {
        return 2; // Index của Favourite
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // BƯỚC 1: Vẫn gọi setContentLayout như cũ.
        // Hàm này sẽ bơm R.layout.activity_favourite vào FrameLayout có id "content_frame"
        setContentLayout(R.layout.activity_favourite);

        // BƯỚC 2: TÌM ĐÚNG "KHUNG CHỨA" TỪ BASEACTIVITY.
        // Bây giờ, ta có thể tìm thấy "content_frame" vì nó là một phần của layout gốc.
        View contentFrame = findViewById(R.id.content_frame);

        // BƯỚC 3: Khởi tạo các thành phần.
        dbHelper = new DatabaseHelper(this);
        // TRUYỀN KHUNG CHỨA ĐÓ VÀO initViews để tìm các view con bên trong nó.
        initViews(contentFrame);
        setupRecyclerView();
        setupToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentUserIdAndProducts();
    }

    /**
     * Phương thức initViews bây giờ nhận vào View cha (content_frame)
     * và tìm kiếm các view con từ chính view cha đó.
     */
    private void initViews(View rootView) {
        // Tìm kiếm bên trong rootView (chính là content_frame)
        toolbar = rootView.findViewById(R.id.toolbar);
        rvFavourites = rootView.findViewById(R.id.rv_favourite_products);
        txtEmptyMessage = rootView.findViewById(R.id.txt_favourite_empty);

        // Kiểm tra để chắc chắn rằng không có view nào bị null
        if (rvFavourites == null || txtEmptyMessage == null || toolbar == null) {
            Log.e(TAG, "Lỗi nghiêm trọng: Không thể tìm thấy các View trong layout activity_favourite.");
            Toast.makeText(this, "Lỗi giao diện, vui lòng thử lại.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new FavouriteAdapter(this, favouriteList, this);
        rvFavourites.setLayoutManager(new LinearLayoutManager(this));
        rvFavourites.setAdapter(adapter);
    }

    private void loadCurrentUserIdAndProducts() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        this.currentUserId = prefs.getInt("userId", -1);

        if (this.currentUserId == -1) {
            Toast.makeText(this, "Vui lòng đăng nhập để xem danh sách.", Toast.LENGTH_LONG).show();
            if (adapter != null) {
                adapter.updateProducts(new ArrayList<>()); // Cập nhật adapter với danh sách rỗng
            }
            checkEmptyState();
            return;
        }

        List<Product> productsFromDb = dbHelper.getFavouriteProducts(currentUserId);
        if (adapter != null) {
            adapter.updateProducts(productsFromDb);
        }
        checkEmptyState();
    }

    private void checkEmptyState() {
        if (adapter == null || adapter.getItemCount() == 0) {
            rvFavourites.setVisibility(View.GONE);
            txtEmptyMessage.setVisibility(View.VISIBLE);
        } else {
            rvFavourites.setVisibility(View.VISIBLE);
            txtEmptyMessage.setVisibility(View.GONE);
        }
    }

    // --- Các sự kiện click từ Adapter ---

    @Override
    public void onProductClicked(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }

    @Override
    public void onRemoveFavouriteClicked(Product product, int position) {
        dbHelper.removeFromWishlist(currentUserId, product.getProductId());

        // Lấy danh sách trực tiếp từ adapter và xóa
        adapter.getProductList().remove(position);
        adapter.notifyItemRemoved(position);
        // Cần cập nhật lại vị trí của các item còn lại
        adapter.notifyItemRangeChanged(position, adapter.getItemCount());

        Toast.makeText(this, "Đã xóa '" + product.getName() + "' khỏi danh sách yêu thích", Toast.LENGTH_SHORT).show();
        checkEmptyState();
    }
}