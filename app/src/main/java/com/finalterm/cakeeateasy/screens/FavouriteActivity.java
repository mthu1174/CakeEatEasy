package com.finalterm.cakeeateasy.screens;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.FavouriteAdapter;
import com.finalterm.cakeeateasy.models.Product;
import com.finalterm.cakeeateasy.screens.BaseActivity;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends BaseActivity {

    RecyclerView rvFavourite;
    FavouriteAdapter adapter; // Tạo một Adapter mới
    List<Product> favouriteList;

    @Override
    public int getNavItemIndex() {
        return 2; // Vị trí của tab "Favourite"
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentLayout(R.layout.activity_favourite);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        rvFavourite = findViewById(R.id.rv_favourite_products);
        rvFavourite.setLayoutManager(new LinearLayoutManager(this));

        loadFavouriteProducts();
        adapter = new FavouriteAdapter(this, favouriteList);
        rvFavourite.setAdapter(adapter);
    }

    private void loadFavouriteProducts() {
        favouriteList = new ArrayList<>();
        // TODO: Lấy danh sách sản phẩm yêu thích từ database/API
        // Dữ liệu giả:
        favouriteList.add(new Product("Orchid Divine", "Vani & Strawberry", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        favouriteList.add(new Product("Signature Love", "Red Velvet", "500.000 đ", "550.000 đ", "10% off", R.drawable.placeholder_cake_promo));
        favouriteList.add(new Product("Jasmine", "White Chocolate", "700.000 đ", null, null, R.drawable.placeholder_cake_promo));
    }
}