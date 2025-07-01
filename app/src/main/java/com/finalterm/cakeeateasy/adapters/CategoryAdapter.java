package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.screens.CategoryActivity;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        final Category category = categoryList.get(position);
        holder.tvCategoryName.setText(category.getName());

        // === SỬA LỖI QUAN TRỌNG ===
        // Dùng hàm getImage() để lấy URL (String) thay vì getImageResId() (int)
        Glide.with(context)
                .load(category.getImage()) // <-- ĐÃ SỬA
                .placeholder(R.drawable.placeholder_cake_promo)
                .error(R.drawable.placeholder_cake_promo)
                .into(holder.ivCategoryImage);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCategoryClick(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}