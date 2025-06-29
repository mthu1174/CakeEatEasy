package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Product;
import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {

    private Context context;
    private List<Product> productList;

    public FavouriteAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
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

        holder.imgProductImage.setImageResource(product.getImageRes());
        holder.txtProductName.setText(product.getName());
        holder.txtProductDesc.setText(product.getDescription());
        holder.txtProductPrice.setText(product.getPrice());

        if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
            holder.txtProductDiscount.setText(product.getDiscount());
            holder.txtProductOldPrice.setText(product.getOldPrice());
            holder.txtProductOldPrice.setPaintFlags(holder.txtProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.txtProductDiscount.setVisibility(View.VISIBLE);
            holder.txtProductOldPrice.setVisibility(View.VISIBLE);
        } else {
            holder.txtProductDiscount.setVisibility(View.GONE);
            holder.txtProductOldPrice.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    // ==========================================================
    // LỚP VIEW HOLDER ĐÃ ĐƯỢC SỬA LỖI
    // ==========================================================
    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductImage;
        TextView txtProductName, txtProductDesc, txtProductPrice, txtProductDiscount, txtProductOldPrice;
        ImageView btnAddToCart, btnFavorite;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);

            // Thêm đầy đủ các lệnh findViewById ở đây
            imgProductImage = itemView.findViewById(R.id.img_product_image);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtProductDesc = itemView.findViewById(R.id.txt_product_desc);
            txtProductPrice = itemView.findViewById(R.id.txt_product_price);
            txtProductDiscount = itemView.findViewById(R.id.txt_product_discount);
            txtProductOldPrice = itemView.findViewById(R.id.txt_product_old_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnFavorite = itemView.findViewById(R.id.btn_add_to_favourite);
        }
    }
    // ==========================================================
}