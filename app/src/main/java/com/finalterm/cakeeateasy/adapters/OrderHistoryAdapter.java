
package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Order;

import java.util.List;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryAdapter.OrderViewHolder> {

    private Context context;
    private List<Order> orderList;

    public OrderHistoryAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    // Hàm để cập nhật danh sách và làm mới adapter
    public void updateOrders(List<Order> newOrderList) {
        this.orderList = newOrderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        // Gán dữ liệu chung
        holder.txtOrderId.setText(order.getId());
        holder.txtOrderDate.setText(order.getDate());
        holder.txtProductName.setText(order.getProductName());
        holder.txtOrderPrice.setText(order.getPrice());
        Glide.with(context).load(order.getProductImageRes()).into(holder.imgProduct);

        // === LOGIC HIỂN THỊ DỰA TRÊN TRẠNG THÁI ===
        String status = order.getStatus();
        if ("Ongoing".equalsIgnoreCase(status)) {
            // Hiển thị nút cho "Ongoing"
            holder.layoutButtonsOngoing.setVisibility(View.VISIBLE);
            holder.layoutButtonsHistory.setVisibility(View.GONE);
            holder.txtStatusTag.setVisibility(View.GONE); // Ẩn tag status
        } else {
            // Hiển thị nút cho "History" (Completed, Cancelled)
            holder.layoutButtonsOngoing.setVisibility(View.GONE);
            holder.layoutButtonsHistory.setVisibility(View.VISIBLE);
            holder.txtStatusTag.setVisibility(View.VISIBLE); // Hiện tag status
            holder.txtStatusTag.setText(status);

            // Đổi màu cho tag status
            if ("Completed".equalsIgnoreCase(status)) {
                holder.txtStatusTag.setBackgroundResource(R.drawable.status_tag_background_green);
            } else { // Ví dụ "Cancelled"
                holder.txtStatusTag.setBackgroundResource(R.drawable.status_tag_background_red);
            }
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView txtOrderId, txtOrderDate, txtProductName, txtOrderPrice, txtStatusTag;
        ImageView imgProduct;
        LinearLayout layoutButtonsOngoing, layoutButtonsHistory;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            txtOrderId = itemView.findViewById(R.id.txt_order_id);
            txtOrderDate = itemView.findViewById(R.id.txt_order_date);
            txtProductName = itemView.findViewById(R.id.txt_product_name);
            txtOrderPrice = itemView.findViewById(R.id.txt_order_price);
            txtStatusTag = itemView.findViewById(R.id.txt_status_tag);
            imgProduct = itemView.findViewById(R.id.img_product);
            layoutButtonsOngoing = itemView.findViewById(R.id.layout_buttons_ongoing);
            layoutButtonsHistory = itemView.findViewById(R.id.layout_buttons_history);
        }
    }
}