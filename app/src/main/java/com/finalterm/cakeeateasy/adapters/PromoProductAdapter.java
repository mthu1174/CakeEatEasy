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

public class PromoProductAdapter extends RecyclerView.Adapter<PromoProductAdapter.PromoViewHolder> {

    private Context context;
    private List<Product> productList;

    public PromoProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public PromoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_promo_product, parent, false);
        return new PromoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PromoViewHolder holder, int position) {
        Product product = productList.get(position);

        holder.ivProductImage.setImageResource(product.getImageRes());
        holder.tvProductName.setText(product.getName());
        holder.tvProductDescription.setText(product.getDescription());
        holder.tvProductPrice.setText(product.getPrice());
        holder.tvProductDiscount.setText(product.getDiscount());
        holder.tvProductOldPrice.setText(product.getOldPrice());

        // Thêm dòng này để gạch ngang giá cũ
        holder.tvProductOldPrice.setPaintFlags(holder.tvProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class PromoViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImage;
        TextView tvProductName, tvProductDescription, tvProductPrice, tvProductDiscount, tvProductOldPrice;

        public PromoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImage = itemView.findViewById(R.id.imgProductSquare);
            tvProductName = itemView.findViewById(R.id.tvProductNameSquare);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescSquare);
            tvProductPrice = itemView.findViewById(R.id.tvProductPriceSquare);
            tvProductDiscount = itemView.findViewById(R.id.tvProductDiscountSquare);
            tvProductOldPrice = itemView.findViewById(R.id.tvProductOldPriceSquare);
        }
    }
}
