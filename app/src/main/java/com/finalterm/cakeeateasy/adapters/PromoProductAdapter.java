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

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Product;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class PromoProductAdapter extends RecyclerView.Adapter<PromoProductAdapter.PromoViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnPromoProductClickListener listener;

    public interface OnPromoProductClickListener {
        void onPromoProductClick(Product product);
    }

    public PromoProductAdapter(Context context, List<Product> productList, OnPromoProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promo_product, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        final Product product = productList.get(position);

        // --- SỬA LỖI ---
        holder.tvProductName.setText(product.getName());
        holder.tvProductCate.setText(product.getCategoryName());

        // Load ảnh từ URL
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_cake_promo)
                .into(holder.ivProductImage);

        // Định dạng giá tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.tvProductPrice.setText(currencyFormat.format(product.getPrice()));

        // Xử lý giá gốc và giảm giá
        if (product.getOriginalPrice() > 0 && product.getOriginalPrice() > product.getPrice()) {
            holder.tvProductOldPrice.setVisibility(View.VISIBLE);
            holder.tvProductOldPrice.setText(currencyFormat.format(product.getOriginalPrice()));
            holder.tvProductOldPrice.setPaintFlags(holder.tvProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int discountPercent = (int) Math.round(((product.getOriginalPrice() - product.getPrice()) / product.getOriginalPrice()) * 100);
            if (discountPercent > 0) {
                holder.tvProductDiscount.setVisibility(View.VISIBLE);
                holder.tvProductDiscount.setText(discountPercent + "% OFF");
            } else {
                holder.tvProductDiscount.setVisibility(View.GONE);
            }
        } else {
            holder.tvProductOldPrice.setVisibility(View.GONE);
            holder.tvProductDiscount.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class PromoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductCate, tvProductPrice, tvProductOldPrice, tvProductDiscount;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo các ID này khớp với file item_promo_product.xml
            ivProductImage = itemView.findViewById(R.id.imgProductSquare);
            tvProductName = itemView.findViewById(R.id.tvProductNameSquare);
            tvProductCate = itemView.findViewById(R.id.tvProductCateSquare);
            tvProductPrice = itemView.findViewById(R.id.tvProductPriceSquare);
            tvProductOldPrice = itemView.findViewById(R.id.tvProductOldPriceSquare);
            tvProductDiscount = itemView.findViewById(R.id.tvProductDiscountSquare);
        }
    }
}