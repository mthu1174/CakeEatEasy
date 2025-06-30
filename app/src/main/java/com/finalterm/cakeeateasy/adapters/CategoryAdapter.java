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

import com.bumptech.glide.Glide; // Thêm thư viện Glide để load ảnh mượt hơn
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Category;
import com.finalterm.cakeeateasy.screens.CategoryActivity; // Import Activity đích

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private Context context;
    private List<Category> categoryList;
    private OnCategoryClickListener listener; // Biến để lưu trữ "người lắng nghe"

    // 1. TẠO INTERFACE: Định nghĩa một "hợp đồng" cho sự kiện click.
    // Bất kỳ class nào (ví dụ: MainActivity) muốn lắng nghe sự kiện này
    // sẽ phải triển khai (implement) interface này.
    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    // 2. SỬA CONSTRUCTOR: Thêm một tham số để nhận vào "người lắng nghe".
    public CategoryAdapter(Context context, List<Category> categoryList, OnCategoryClickListener listener) {
        this.context = context;
        this.categoryList = categoryList;
        this.listener = listener; // Gán "người lắng nghe" được truyền vào.
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        // Lấy category tại vị trí hiện tại
        final Category category = categoryList.get(position);

        // Gán dữ liệu lên view
        holder.tvCategoryName.setText(category.getName());

        // Sử dụng Glide để load ảnh hiệu quả hơn là setImageResource
        Glide.with(context)
                .load(category.getImageResId()) // Lấy ID resource từ model
                .placeholder(R.drawable.placeholder_cake_promo) // Ảnh hiển thị trong lúc chờ load
                .error(R.drawable.placeholder_cake_promo) // Ảnh hiển thị nếu load lỗi
                .into(holder.ivCategoryImage);

        // 3. THIẾT LẬP SỰ KIỆN CLICK
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kiểm tra xem "người lắng nghe" có tồn tại không (để tránh NullPointerException)
                if (listener != null) {
                    // Nếu có, gọi phương thức trong interface và truyền đối tượng category đã được click.
                    // Việc xử lý logic tiếp theo (chuyển màn hình) sẽ do "người lắng nghe" (MainActivity) quyết định.
                    listener.onCategoryClick(category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    // ViewHolder không có gì thay đổi
    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryImage;
        TextView tvCategoryName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            // Giả sử ID trong item_category.xml là như này
            ivCategoryImage = itemView.findViewById(R.id.iv_category_image);
            tvCategoryName = itemView.findViewById(R.id.tv_category_name);
        }
    }
}