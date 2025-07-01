package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * MainActivity - Màn hình chính của ứng dụng.
 * Kế thừa từ BaseActivity để có thanh điều hướng chung.
 * Lắng nghe sự kiện click từ 3 loại RecyclerView: Category, PromoProduct, và Voucher.
 */
public class MainActivity extends BaseActivity implements
        CategoryAdapter.OnCategoryClickListener,
        PromoProductAdapter.OnPromoProductClickListener, // THÊM DÒNG NÀY
        VoucherGridAdapter.OnVoucherClickListener {

    private static final String TAG = "MainActivity";

    // UI Components
    private TextView txtWelcomeUser;
    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutDotsIndicator;
    private RecyclerView rvCollections, rvPromo, rvVouchers;
    private ImageView imgFilterIcon;

    // Adapters
    private BannerAdapter bannerAdapter;
    private CategoryAdapter categoryAdapter;
    private PromoProductAdapter promoAdapter;
    private VoucherGridAdapter voucherAdapter;

    // Data lists
    private List<BannerItem> bannerItemList;
    private List<Category> categoryList;
    private List<Product> promoProductList;
    private List<Voucher> voucherList;

    // Others
    private final Handler slideHandler = new Handler(Looper.getMainLooper());
    private Runnable slideRunnable;
    private DatabaseHelper dbHelper;
    private Button btnViewAllProducts;

    @Override
    public int getNavItemIndex() {
        return 0; // Màn hình Home có index là 0
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_main);

        dbHelper = new DatabaseHelper(this);
        initViews();
        loadAllDataFromDb();
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
        findViewById(R.id.btn_view_all_products).setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProductListActivity.class);
            startActivity(intent);
            });
        }

    private void loadAllDataFromDb() {
        seedInitialDataIfNeeded();
        Log.i(TAG, "Loading all data from database...");
        categoryList = dbHelper.getAllCategories();
        promoProductList = dbHelper.getPromoProducts();
        voucherList = dbHelper.getAllVouchers();
        loadBannerData();
        // TODO: Lấy tên người dùng thật từ SharedPreferences sau khi đăng nhập
        txtWelcomeUser.setText("Nha Linh!");
    }

    private void seedInitialDataIfNeeded() {
        if (dbHelper.getCategoriesCount() == 0) {
            Log.d(TAG, "Database is empty. Seeding initial Categories.");
            dbHelper.addCategory(new Category("Birthday", "https://i.ibb.co/L05k9wN/category-birthday.png"));
            dbHelper.addCategory(new Category("Wedding", "https://i.ibb.co/Y0qB2c0/category-wedding.png"));
            dbHelper.addCategory(new Category("Vanilla", "https://i.ibb.co/yQj0D1V/category-vanilla.png"));
            dbHelper.addCategory(new Category("Fruit", "https://i.ibb.co/2Z5hSWM/category-fruit.png"));
        }
        if (dbHelper.getProductsCount() == 0) {
            Log.d(TAG, "Database is empty. Seeding initial Products.");
            // Dữ liệu mẫu đã được thêm vào CSDL từ file assets
        }
        if (dbHelper.getVouchersCount() == 0) {
            Log.d(TAG, "Database is empty. Seeding initial Vouchers.");
            dbHelper.addVoucher(new Voucher("New Member", "10% off", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_gift, "NEWMEMBER", 100000));
            dbHelper.addVoucher(new Voucher("Freeshipping", "Order 500k+", R.color.voucher_green, R.color.voucher_icon_bg_green, R.drawable.ic_shopping_cart, "FREESHIP", 30000));
        }
    }

    private void loadBannerData() {
        bannerItemList = new ArrayList<>();
        bannerItemList.add(new BannerItem(R.drawable.banner_fresh_cake, "Fresh Cake", "Buy now!"));
        bannerItemList.add(new BannerItem(R.drawable.banner_promo, "Special Promo", "View All"));
        bannerItemList.add(new BannerItem(R.drawable.banner_new_arrivals, "New Arrivals", "Discover"));
    }

    private void setupRecyclerViews() {
        // Khởi tạo list rỗng để tránh NullPointerException nếu DB lỗi
        if (categoryList == null) categoryList = new ArrayList<>();
        if (promoProductList == null) promoProductList = new ArrayList<>();
        if (voucherList == null) voucherList = new ArrayList<>();

        // Setup Category RecyclerView
        rvCollections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this, categoryList, this);
        rvCollections.setAdapter(categoryAdapter);

        // Setup Promo Product RecyclerView (SỬA Ở ĐÂY)
        rvPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        promoAdapter = new PromoProductAdapter(this, promoProductList, this); // Truyền 'this' làm listener
        rvPromo.setAdapter(promoAdapter);

        // Setup Voucher RecyclerView
        rvVouchers.setLayoutManager(new GridLayoutManager(this, 2));
        voucherAdapter = new VoucherGridAdapter(this, voucherList, this);
        rvVouchers.setAdapter(voucherAdapter);
    }

    /**
     * Xử lý sự kiện khi người dùng click vào một danh mục (collection).
     */
    @Override
    public void onCategoryClick(Category category) {
        Intent intent = new Intent(this, CategoryActivity.class);
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_ID, category.getCategoryId());
        intent.putExtra(CategoryActivity.EXTRA_CATEGORY_NAME, category.getName());
        startActivity(intent);
    }

    /**
     * Xử lý sự kiện khi người dùng click vào một sản phẩm khuyến mãi.
     */
    @Override
    public void onPromoProductClick(Product product) {
        Intent intent = new Intent(this, ProductDetailsActivity.class);
        intent.putExtra(ProductDetailsActivity.EXTRA_PRODUCT_ID, product.getProductId());
        startActivity(intent);
    }

    /**
     * Xử lý sự kiện khi người dùng click vào một voucher.
     */
    @Override
    public void onVoucherClick(Voucher voucher) {
        // Tạm thời hiển thị Toast, sau này có thể mở màn hình chi tiết voucher
        Toast.makeText(this, "Bạn đã chọn voucher: " + voucher.getTitle(), Toast.LENGTH_SHORT).show();
    }


    // --- CÁC HÀM CHO BANNER SLIDER (KHÔNG THAY ĐỔI) ---
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