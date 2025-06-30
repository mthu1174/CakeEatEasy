package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.BannerAdapter;
import com.finalterm.cakeeateasy.adapters.CategoryAdapter;
import com.finalterm.cakeeateasy.adapters.PromoProductAdapter;
import com.finalterm.cakeeateasy.adapters.VoucherGridAdapter;
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.BannerItem;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.models.Voucher;
import com.finalterm.cakeeateasy.screens.dialogs.FilterFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends com.finalterm.cakeeateasy.screens.BaseActivity implements CategoryAdapter.OnCategoryClickListener {

    // === KHAI BÁO BIẾN ===
    private TextView txtWelcomeUser;
    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutDotsIndicator;
    private RecyclerView rvCollections, rvPromo, rvVouchers;
    private ImageView imgFilterIcon;

    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private PromoProductAdapter promoAdapter;
    private VoucherGridAdapter voucherAdapter;

    private List<BannerItem> bannerItemList;
    private List<Category> categoryList;
    private List<Product> promoProductList;
    private List<Voucher> voucherList;

    private final Handler slideHandler = new Handler(Looper.getMainLooper());
    private Runnable slideRunnable;

    // Biến cho Database Helper
    private DatabaseHelper dbHelper;

    @Override
    public int getNavItemIndex() {
        return 0; // Index của item Home trong Bottom Navigation
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_main);

        // Khởi tạo DatabaseHelper ngay từ đầu
        dbHelper = new DatabaseHelper(this);

        initViews();
        loadDataFromSources(); // Đổi tên hàm để phản ánh đúng logic
        setupRecyclerViews();
        setupBannerSlider();
        setupFilterButton();
    }

    private void initViews() {
        txtWelcomeUser = findViewById(R.id.txt_welcome_user);
        viewPagerBanner = findViewById(R.id.view_pager_banner);
        layoutDotsIndicator = findViewById(R.id.layout_dots_indicator);
        rvCollections = findViewById(R.id.rv_collections);
        rvPromo = findViewById(R.id.rv_promo);
        rvVouchers = findViewById(R.id.rv_vouchers);
        imgFilterIcon = findViewById(R.id.img_filter_icon);
    }

    /**
     * Hàm chính để nạp dữ liệu.
     * Sẽ kiểm tra và nạp dữ liệu ban đầu cho Category vào DB nếu cần.
     * Sau đó load tất cả dữ liệu cần thiết cho màn hình.
     */
    private void loadDataFromSources() {
        // 1. Xử lý dữ liệu cho Category từ Database
        if (dbHelper.getCategoriesCount() == 0) {
            Log.d("MainActivity_DB", "Database is empty. Seeding initial data for Categories.");
            seedInitialCategories();
        } else {
            Log.d("MainActivity_DB", "Database already has data for Categories. Skipping seeding.");
        }
        // Luôn load dữ liệu category từ DB
        categoryList = dbHelper.getAllCategories();

        // 2. Nạp dữ liệu cứng cho các phần còn lại (Banner, Promo, Voucher)
        loadHardcodedData();
    }

    /**
     * Hàm này chỉ chứa dữ liệu ban đầu để thêm vào DB một lần duy nhất.
     */
    private void seedInitialCategories() {
        dbHelper.addCategory(new Category("Birthday", R.drawable.placeholder_cake_promo));
        dbHelper.addCategory(new Category("Wedding", R.drawable.placeholder_cake_promo));
        dbHelper.addCategory(new Category("Vanilla", R.drawable.placeholder_cake_promo));
        dbHelper.addCategory(new Category("Fruit", R.drawable.placeholder_cake_promo));
        dbHelper.addCategory(new Category("Chocolate", R.drawable.placeholder_cake_promo));
    }

    /**
     * Hàm này nạp các dữ liệu không lưu trong DB (hoặc chưa cần lưu).
     */
    private void loadHardcodedData() {
        txtWelcomeUser.setText("Nha Linh!");

        // Dữ liệu cho banner
        bannerItemList = new ArrayList<>();
        bannerItemList.add(new BannerItem(R.drawable.banner_fresh_cake, "Fresh Cake", "Buy now!"));
        bannerItemList.add(new BannerItem(R.drawable.banner_promo, "Special Promo", "View All"));
        bannerItemList.add(new BannerItem(R.drawable.banner_new_arrivals, "New Arrivals", "Discover"));

        // Dữ liệu promo
        promoProductList = new ArrayList<>();
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Karl", "Vani & Yogurt", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));

        // Dữ liệu voucher
        voucherList = new ArrayList<>();
        voucherList.add(new Voucher("New Member", "10% off", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_gift, "NEWMEMBER", 100000));
        voucherList.add(new Voucher("Freeshipping", "Order 500k+", R.color.voucher_green, R.color.voucher_icon_bg_green, R.drawable.ic_shopping_cart, "FREESHIP", 30000));
        voucherList.add(new Voucher("Happy Valentine", "10% off", R.color.home_pink, R.color.voucher_icon_bg_pink, R.drawable.ic_favorite_doutone, "VALENTINE", 100000));
        voucherList.add(new Voucher("Holiday day!", "Buy 1 get 1", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_sun, "HOLIDAY", 200000));
    }

    private void setupRecyclerViews() {
        // Khởi tạo các list để tránh NullPointerException nếu DB trống
        if (categoryList == null) categoryList = new ArrayList<>();
        if (promoProductList == null) promoProductList = new ArrayList<>();
        if (voucherList == null) voucherList = new ArrayList<>();

        // Thiết lập RecyclerView cho Collections (DANH MỤC)
        rvCollections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        rvCollections.setAdapter(categoryAdapter);

        // Thiết lập các RecyclerView khác
        rvPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        promoAdapter = new PromoProductAdapter(this, promoProductList);
        rvPromo.setAdapter(promoAdapter);

        rvVouchers.setLayoutManager(new GridLayoutManager(this, 2));
        voucherAdapter = new VoucherGridAdapter(this, voucherList);
        rvVouchers.setAdapter(voucherAdapter);
    }

    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_NAME, category.getName());
        startActivity(intent);
    }

    // --- CÁC HÀM KHÁC KHÔNG THAY ĐỔI ---
    private void setupBannerSlider() {
        if (bannerItemList == null) bannerItemList = new ArrayList<>();
        bannerAdapter = new BannerAdapter(bannerItemList);
        viewPagerBanner.setAdapter(bannerAdapter);
        viewPagerBanner.setOffscreenPageLimit(3);
        setupDotIndicator();
        slideRunnable = () -> {
            int currentItem = viewPagerBanner.getCurrentItem() + 1;
            if (currentItem >= bannerAdapter.getItemCount()) {
                currentItem = 0;
            }
            viewPagerBanner.setCurrentItem(currentItem, true);
        };
        viewPagerBanner.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateDotIndicator(position);
                slideHandler.removeCallbacks(slideRunnable);
                slideHandler.postDelayed(slideRunnable, 3000);
            }
        });
    }

    private void setupFilterButton() {
        imgFilterIcon.setOnClickListener(v -> openFilterFragment());
    }

    private void openFilterFragment() {
        FilterFragment filterFragment = new FilterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.slide_in_up, R.anim.fade_out, R.anim.fade_in, R.anim.slide_out_down);
        transaction.add(R.id.fragment_container, filterFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void setupDotIndicator() {
        if (bannerAdapter == null || bannerAdapter.getItemCount() == 0) return;
        layoutDotsIndicator.removeAllViews();
        ImageView[] dots = new ImageView[bannerAdapter.getItemCount()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_default));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 0, 8, 0);
            dots[i].setLayoutParams(params);
            layoutDotsIndicator.addView(dots[i]);
        }
        updateDotIndicator(0);
    }

    private void updateDotIndicator(int index) {
        if (bannerAdapter == null || bannerAdapter.getItemCount() == 0) return;
        for (int i = 0; i < layoutDotsIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutDotsIndicator.getChildAt(i);
            if (i == index) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_selected));
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_default));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (slideRunnable != null) {
            slideHandler.removeCallbacks(slideRunnable);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (slideRunnable != null) {
            slideHandler.postDelayed(slideRunnable, 3000);
        }
    }
}