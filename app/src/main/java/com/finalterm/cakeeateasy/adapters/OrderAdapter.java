package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Order;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;
    private OrderItemListener listener;

    // BƯỚC 1: Định nghĩa interface để giao tiếp với Activity
    public interface OrderItemListener {
        void onOrderClicked(Order order);
        void onReorderClicked(Order order);
        void onReviewClicked(Order order);
    }

    // BƯỚC 2: Cập nhật constructor để nhận listener
    public OrderAdapter(Context context, List<Order> orderList, OrderItemListener listener) {
        this.context = context;
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    // Inside OrderAdapter.java

    // Trong file OrderAdapter.java

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.txtOrderId.setText("#" + order.getOrderId());
        holder.txtOrderDate.setText(formatDateString(order.getDate()));
        holder.txtProductName.setText(order.getFirstProductName());
        holder.txtTotal.setText(order.getTotalPrice());
        holder.txtStatus.setText(order.getStatus());

        // === GIẢI THÍCH VÀ SỬA LỖI CHO TEXTVIEW STATUS ===
        // 1. Đặt màu cho chữ của status
        // 2. Đặt màu nền (background) cho status
        if ("Completed".equalsIgnoreCase(order.getStatus())) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtStatus.setBackgroundResource(R.drawable.status_tag_background_green);
        } else if ("Cancelled".equalsIgnoreCase(order.getStatus())) {
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtStatus.setBackgroundResource(R.drawable.status_tag_background_red);
        } else { // Ongoing
            holder.txtStatus.setTextColor(context.getResources().getColor(R.color.white));
            holder.txtStatus.setBackgroundResource(R.drawable.status_tag_background_orange);
        }

        // Load ảnh sản phẩm (Giữ nguyên)
        String imageUrl = order.getFirstProductImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.placeholder_cake_promo)
                    .error(R.drawable.placeholder_cake_promo)
                    .into(holder.imgProduct);
        } else {
            holder.imgProduct.setImageResource(R.drawable.placeholder_cake_promo);
        }

        // Gán sự kiện click (Giữ nguyên)
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onOrderClicked(order);
        });
        holder.btnReorder.setOnClickListener(v -> {
            if (listener != null) listener.onReorderClicked(order);
        });
        holder.btnReview.setOnClickListener(v -> {
            if ("Completed".equalsIgnoreCase(order.getStatus())) {
                if (listener != null) listener.onReviewClicked(order);
            }
        });

        // Ẩn/hiện nút Review dựa trên trạng thái
        holder.btnReview.setVisibility("Completed".equalsIgnoreCase(order.getStatus()) ? View.VISIBLE : View.GONE);
    }

    private String formatDateString(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return ""; // Trả về chuỗi rỗng nếu không có dữ liệu
        }

        // Định dạng của chuỗi ngày tháng gốc trong CSDL
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        // Định dạng mong muốn để hiển thị
        SimpleDateFormat outputFormat = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());

        try {
            Date date = inputFormat.parse(dateString);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            // Nếu có lỗi parse, trả về chuỗi gốc để debug
            return dateString;
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public void updateOrders(List<Order> newOrders) {
        this.orderList.clear();
        this.orderList.addAll(newOrders);
        notifyDataSetChanged();
    }

    // ViewHolder
    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView txtOrderId, txtOrderDate, txtProductName, txtTotal, txtStatus;
        Button btnReorder, btnReview;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            imgProduct = itemView.findViewById(R.id.img_order_product);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderDate = itemView.findViewById(R.id.txt_order_date);
            txtProductName = itemView.findViewById(R.id.txt_order_product_name);
            txtTotal = itemView.findViewById(R.id.txt_order_total);
            txtStatus = itemView.findViewById(R.id.txt_order_status);
            btnReorder = itemView.findViewById(R.id.btn_reorder);
            btnReview = itemView.findViewById(R.id.btn_review);
        }
    }
}