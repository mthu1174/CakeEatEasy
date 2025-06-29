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

public class ProductGridAdapter extends RecyclerView.Adapter<ProductGridAdapter.ProductViewHolder> {

    private Context context;
    private List<Product> productList;

    public ProductGridAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Tham chiếu đến layout item mới của chúng ta
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_grid, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);

        // Gán dữ liệu vào các View
        holder.imgProductImage.setImageResource(product.getImageRes());
        holder.txtProductName.setText(product.getName());
        holder.txtProductDesc.setText(product.getDescription());
        holder.txtProductPrice.setText(product.getPrice());

        // Xử lý hiển thị phần giảm giá và giá cũ
        // Nếu không có giảm giá (discount is null hoặc rỗng), ẩn các view liên quan
        if (product.getDiscount() != null && !product.getDiscount().isEmpty()) {
            holder.txtProductDiscount.setText(product.getDiscount());
            holder.txtProductOldPrice.setText(product.getOldPrice());

            // Gạch ngang giá cũ
            holder.txtProductOldPrice.setPaintFlags(holder.txtProductOldPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            // Hiển thị các view này
            holder.txtProductDiscount.setVisibility(View.VISIBLE);
            holder.txtProductOldPrice.setVisibility(View.VISIBLE);
        } else {
            // Nếu không có giảm giá, ẩn các view này đi
            holder.txtProductDiscount.setVisibility(View.GONE);
            holder.txtProductOldPrice.setVisibility(View.GONE);
        }

        // TODO: Thêm sự kiện onClick cho các nút nếu cần
        // holder.btnAddToCart.setOnClickListener(...);
        // holder.btnFavorite.setOnClickListener(...);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các View với quy ước đặt tên mới
        ImageView imgProductImage;
        TextView txtProductName, txtProductDesc, txtProductPrice, txtProductDiscount, txtProductOldPrice;
        ImageView btnAddToCart, btnFavorite;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            // Tham chiếu đến các View bằng ID
            imgProductImage = itemView.findViewById(R.id.imgProductSquare);
            txtProductName = itemView.findViewById(R.id.tvProductNameSquare);
            txtProductDesc = itemView.findViewById(R.id.tvProductDescSquare);
            txtProductPrice = itemView.findViewById(R.id.tvProductPriceSquare);
            txtProductDiscount = itemView.findViewById(R.id.tvProductDiscountSquare);
            txtProductOldPrice = itemView.findViewById(R.id.tvProductOldPriceSquare);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCartSquare);
            btnFavorite = itemView.findViewById(R.id.btnFavoriteSquare);
        }
    }
}