package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private List<CartItem> cartItems;
    private Context context;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQuantity);
        void onItemRemoved(CartItem item, int position);
        void onItemSelectionChanged();
    }

    public CartAdapter(Context context, List<CartItem> cartItems, CartItemListener listener) {
        this.context = context;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    public void updateCartItems(List<CartItem> newCartItems) {
        this.cartItems = newCartItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // SỬA LỖI: Đặt tên file layout đúng là 'item_cart.xml'
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_product, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = cartItems.get(position);

        // Gán dữ liệu vào các View
        holder.edtName.setText(item.getName());
        holder.edtQuantity.setText(String.valueOf(item.getQuantity()));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.edtPrice.setText(currencyFormat.format(item.getPrice()));

        Glide.with(context)
                .load(item.getImageUrl())
                .placeholder(R.drawable.placeholder_cake_promo)
                .error(R.drawable.placeholder_cake_promo)
                .into(holder.imgProduct);

        // Xử lý Checkbox
        holder.checkboxItem.setOnCheckedChangeListener(null);
        holder.checkboxItem.setChecked(item.isSelected());
        holder.checkboxItem.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                item.setSelected(isChecked);
                if (listener != null) listener.onItemSelectionChanged();
            }
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            holder.edtQuantity.setText(String.valueOf(newQuantity));
            if (listener != null) listener.onQuantityChanged(item, newQuantity);
        });

        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                holder.edtQuantity.setText(String.valueOf(newQuantity));
                if (listener != null) listener.onQuantityChanged(item, newQuantity);
            }
        });

        // Hiện tại layout không có nút xóa, nên không cần xử lý.
        // Nếu bạn muốn thêm nút xóa, hãy thêm một ImageView với id="btn_remove" vào layout.
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    // === SỬA LỖI: Sửa lại ViewHolder để khớp với ID và kiểu View trong XML ===
    static class CartViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxItem;
        ImageView imgProduct;
        EditText edtName, edtPrice, edtQuantity; // Đổi thành EditText
        ImageView btnDecrease, btnIncrease; // Đổi thành ImageView

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxItem = itemView.findViewById(R.id.checkbox); // Sửa ID
            imgProduct = itemView.findViewById(R.id.img_product); // Sửa ID
            edtName = itemView.findViewById(R.id.edt_product_name); // Sửa ID và kiểu
            edtPrice = itemView.findViewById(R.id.edt_product_price); // Sửa ID và kiểu
            edtQuantity = itemView.findViewById(R.id.edt_quantity); // Sửa ID và kiểu
            btnDecrease = itemView.findViewById(R.id.btn_decrease); // Sửa ID
            btnIncrease = itemView.findViewById(R.id.btn_increase); // Sửa ID
        }
    }
    private List<CartItem> cartItemList;
    // ... các biến khác và constructor

    // ... các phương thức khác như onCreateViewHolder, onBindViewHolder...

    // === THÊM HÀM NÀY VÀO ===
    /**
     * Trả về danh sách các sản phẩm hiện tại mà adapter đang nắm giữ.
     * @return List<CartItem>
     */
    public List<CartItem> getCartItems() {
        return this.cartItemList;
    }
}