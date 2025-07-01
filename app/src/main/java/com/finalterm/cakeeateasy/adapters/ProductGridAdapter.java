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

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;
    private OnProductClickListener listener; // Biến để lưu trữ "người lắng nghe"

    /**
     * Interface để định nghĩa một "hợp đồng" cho sự kiện click.
     * Activity nào sử dụng adapter này sẽ phải implement interface này để nhận sự kiện.
     */
    public interface OnProductClickListener {
        void onProductClick(Product product);
    }

    /**
     * Constructor đã được cập nhật để nhận vào một listener.
     */
    public ProductGridAdapter(Context context, List<Product> productList, OnProductClickListener listener) {
        this.context = context;
        this.productList = productList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Load ảnh từ URL bằng Glide
        Glide.with(context)
                .load(product.getImageUrl())
                .placeholder(R.drawable.placeholder_cake_promo)
                .error(R.drawable.placeholder_cake_promo)
                .into(holder.imgProductImage);

        // Gán dữ liệu vào các TextView
        holder.txtProductName.setText(product.getName());
        holder.txtProductDesc.setText(product.getDescription());

        // Định dạng giá tiền
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        holder.txtProductPrice.setText(currencyFormat.format(product.getPrice()));

        // Xử lý hiển thị giá gốc và gạch ngang
        if (product.getOriginalPrice() > 0 && product.getOriginalPrice() > product.getPrice()) {
            holder.txtProductOldPrice.setVisibility(View.VISIBLE);
            holder.txtProductOldPrice.setText(currencyFormat.format(product.getOriginalPrice()));
            holder.txtProductOldPrice.setPaintFlags(holder.txtProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.txtProductOldPrice.setVisibility(View.GONE);
        }

        // === THÊM SỰ KIỆN CLICK CHO TOÀN BỘ ITEM VIEW ===
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProductImage;
        TextView txtProductName, txtProductDesc, txtProductPrice, txtProductOldPrice;
        // Các nút bấm như Add to Cart, Favorite có thể được thêm vào đây nếu cần
        // ImageView btnAddToCart, btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Đảm bảo các ID này khớp chính xác với file layout item_product_grid.xml
            imgProductImage = itemView.findViewById(R.id.imgProductSquare);
            txtProductName = itemView.findViewById(R.id.tvProductNameSquare);
            txtProductDesc = itemView.findViewById(R.id.tvProductCateSquare);
            txtProductPrice = itemView.findViewById(R.id.tvProductPriceSquare);
            txtProductOldPrice = itemView.findViewById(R.id.tvProductOldPriceSquare);
            // btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            // btnFavorite = itemView.findViewById(R.id.btn_favorite);
        }
    }
}
