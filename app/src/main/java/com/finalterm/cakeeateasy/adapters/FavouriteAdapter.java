package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnFavouriteInteractionListener listener;

    // Interface để giao tiếp ngược lại với Activity
    public interface OnFavouriteInteractionListener {
        void onProductClicked(Product product);
        void onRemoveFavouriteClicked(Product product, int position);
    }

    public FavouriteAdapter(Context context, List<Product> productList, OnFavouriteInteractionListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.txtName.setText(product.getName());
        holder.txtCategory.setText(product.getCategoryName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.txtPrice.setText(currencyFormat.format(product.getPrice()));

        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_cake_promo) // Thay bằng ảnh mặc định của bạn
                .into(holder.imgProduct);

        // Bắt sự kiện click cho toàn bộ item
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClicked(product);
            }
        });

        // Bắt sự kiện click cho nút xóa
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveFavouriteClicked(product, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // Phương thức quan trọng để cập nhật dữ liệu từ Activity
    public void updateProducts(List<Product> newProducts) {
        this.productList.clear();
        this.productList.addAll(newProducts);
        notifyDataSetChanged();
    }


    // ViewHolder
    // ViewHolder
    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct, btnRemove;
        TextView txtName, txtCategory, txtPrice;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);

            // === SỬA LẠI CÁC ID TẠI ĐÂY CHO KHỚP VỚI item_favourite.xml ===
            imgProduct = itemView.findViewById(R.id.img_favourite_product);
            txtName = itemView.findViewById(R.id.txt_favourite_name);
            txtCategory = itemView.findViewById(R.id.txt_favourite_category);
            txtPrice = itemView.findViewById(R.id.txt_favourite_price);
            btnRemove = itemView.findViewById(R.id.btn_favourite_remove); // Bỏ comment và dùng ID mới
        }
    }
    public List<Product> getProductList() {
        return this.productList;
    }
}