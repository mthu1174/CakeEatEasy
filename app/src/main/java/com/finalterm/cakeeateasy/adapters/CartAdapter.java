package com.finalterm.cakeeateasy.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.CartItem;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<CartItem> cartItems;
    private final OnCartChangeListener listener;

    public interface OnCartChangeListener {
        void onCartChanged();
    }

    public CartAdapter(List<CartItem> cartItems, OnCartChangeListener listener) {
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);
        holder.checkBox.setChecked(item.isSelected());
        holder.imgProduct.setImageResource(item.getImageResId());
        holder.edtProductName.setText(item.getName());
        holder.edtProductPrice.setText(item.getPrice());
        holder.edtProductDiscount.setText(item.getDiscount());
        holder.edtQuantity.setText(String.valueOf(item.getQuantity()));

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            listener.onCartChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                holder.edtQuantity.setText(String.valueOf(item.getQuantity()));
                listener.onCartChanged();
            }
        });

        holder.btnIncrease.setOnClickListener(v -> {
            item.setQuantity(item.getQuantity() + 1);
            holder.edtQuantity.setText(String.valueOf(item.getQuantity()));
            listener.onCartChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        ImageView imgProduct, btnDecrease, btnIncrease;
        EditText edtProductName, edtProductPrice, edtProductDiscount, edtQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            imgProduct = itemView.findViewById(R.id.img_product);
            edtProductName = itemView.findViewById(R.id.edt_product_name);
            edtProductPrice = itemView.findViewById(R.id.edt_product_price);
            edtProductDiscount = itemView.findViewById(R.id.edt_product_discount);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
            edtQuantity = itemView.findViewById(R.id.edt_quantity);
        }
    }
} 