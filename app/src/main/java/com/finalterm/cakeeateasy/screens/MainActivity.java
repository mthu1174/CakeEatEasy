package com.finalterm.cakeeateasy.screens; // Giữ nguyên package của bạn

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.finalterm.cakeeateasy.models.BannerItem;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.models.Voucher;
import com.finalterm.cakeeateasy.screens.dialogs.FilterFragment; // Quan trọng: Đã import FilterFragment

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends com.finalterm.cakeeateasy.screens.BaseActivity {

    // === KHAI BÁO BIẾN ===
    private TextView txtWelcomeUser;
    private ViewPager2 viewPagerBanner;
    private LinearLayout layoutDotsIndicator;
    private RecyclerView rvCollections, rvPromo, rvVouchers;
    private ImageView imgFilterIcon; // BIẾN CHO ICON FILTER

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

    @Override
    public int getNavItemIndex() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_main);

        initViews();
        loadData();
        setupRecyclerViews();
        setupBannerSlider();
        setupFilterButton(); // GỌI HÀM CÀI ĐẶT NÚT FILTER
    }

    private void initViews() {
        txtWelcomeUser = findViewById(R.id.txt_welcome_user);
        viewPagerBanner = findViewById(R.id.view_pager_banner);
        layoutDotsIndicator = findViewById(R.id.layout_dots_indicator);
        rvCollections = findViewById(R.id.rv_collections);
        rvPromo = findViewById(R.id.rv_promo);
        rvVouchers = findViewById(R.id.rv_vouchers);
        imgFilterIcon = findViewById(R.id.img_filter_icon); // KẾT NỐI VỚI ID CỦA ICON
    }

    private void loadData() {
        // Nạp dữ liệu cho banner
        bannerItemList = new ArrayList<>();
        bannerItemList.add(new BannerItem(R.drawable.banner_fresh_cake, "Fresh Cake", "Buy now!"));
        bannerItemList.add(new BannerItem(R.drawable.banner_promo, "Special Promo", "View All"));
        bannerItemList.add(new BannerItem(R.drawable.banner_new_arrivals, "New Arrivals", "Discover"));

        txtWelcomeUser.setText("Nha Linh!");

        // Dữ liệu cho các danh mục khác
        categoryList = new ArrayList<>();
        categoryList.add(new Category("Birthday", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Wedding", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Vanilla", R.drawable.placeholder_cake_promo));
        categoryList.add(new Category("Fruit", R.drawable.placeholder_cake_promo));

        promoProductList = new ArrayList<>();
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Karl", "Vani & Yogurt", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        promoProductList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));

        voucherList = new ArrayList<>();
        voucherList.add(new Voucher("New Member", "10% off", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_gift));
        voucherList.add(new Voucher("Freeshipping", "Order 500k+", R.color.voucher_green, R.color.voucher_icon_bg_green, R.drawable.ic_shopping_cart));
        voucherList.add(new Voucher("Happy Valentine", "10% off", R.color.home_pink, R.color.voucher_icon_bg_pink, R.drawable.ic_favorite_doutone));
        voucherList.add(new Voucher("Holiday day!", "Buy 1 get 1", R.color.voucher_orange, R.color.voucher_icon_bg_orange, R.drawable.ic_sun));
    }

    private void setupRecyclerViews() {
        rvCollections.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(this, categoryList);
        rvCollections.setAdapter(categoryAdapter);

        rvPromo.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        promoAdapter = new PromoProductAdapter(this, promoProductList);
        rvPromo.setAdapter(promoAdapter);

        rvVouchers.setLayoutManager(new GridLayoutManager(this, 2));
        voucherAdapter = new VoucherGridAdapter(this, voucherList);
        rvVouchers.setAdapter(voucherAdapter);
    }

    private void setupBannerSlider() {
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

    /**
     * Hàm mới để cài đặt sự kiện click cho icon Filter.
     */
    private void setupFilterButton() {
        imgFilterIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilterFragment();
            }
        });
    }

    /**
     * Hàm mới để mở FilterFragment.
     */
    private void openFilterFragment() {
        // 1. Tạo một instance mới của FilterFragment
        FilterFragment filterFragment = new FilterFragment();

        // 2. Bắt đầu một "giao dịch" (transaction) để quản lý Fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 3. Thiết lập animation (hiệu ứng) cho Fragment xuất hiện
        transaction.setCustomAnimations(
                R.anim.slide_in_up,   // Animation khi fragment vào
                R.anim.fade_out,      // Animation khi fragment cũ thoát (nếu có)
                R.anim.fade_in,       // Animation khi fragment cũ vào lại
                R.anim.slide_out_down // Animation khi fragment thoát (khi bấm back)
        );

        // 4. Dùng 'add' để đặt FilterFragment LÊN TRÊN MainActivity
        // và trỏ vào container chúng ta đã tạo (R.id.fragment_container)
        transaction.add(R.id.fragment_container, filterFragment);

        // 5. Rất quan trọng: Thêm giao dịch này vào back stack
        // Điều này cho phép người dùng quay lại MainActivity bằng nút Back
        transaction.addToBackStack(null);

        // 6. Thực thi giao dịch
        transaction.commit();
    }

    // --- Các hàm hỗ trợ cho Banner Slider (giữ nguyên) ---

    private void setupDotIndicator() {
        layoutDotsIndicator.removeAllViews();
        ImageView[] dots = new ImageView[bannerAdapter.getItemCount()];
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_default));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(8, 0, 8, 0);
            dots[i].setLayoutParams(params);
            layoutDotsIndicator.addView(dots[i]);
        }
        updateDotIndicator(0);
    }

    private void updateDotIndicator(int index) {
        for (int i = 0; i < layoutDotsIndicator.getChildCount(); i++) {
            ImageView dot = (ImageView) layoutDotsIndicator.getChildAt(i);
            if (i == index) {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_selected));
            } else {
                dot.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.tab_indicator_default));
            }
        }
    }

    // === Vòng đời cho banner slider (giữ nguyên) ===
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