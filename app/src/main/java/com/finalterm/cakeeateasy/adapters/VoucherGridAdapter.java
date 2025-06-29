package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.Voucher;

import java.util.List;

public class VoucherGridAdapter extends RecyclerView.Adapter<VoucherGridAdapter.VoucherViewHolder> {

    private Context context;
    private List<Voucher> voucherList;

    public VoucherGridAdapter(Context context, List<Voucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_grid_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = voucherList.get(position);

        // --- Set dữ liệu cho text ---
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDesc.setText(voucher.getDescription());
        holder.tvTitle.setTextColor(ContextCompat.getColor(context, voucher.getTitleAndIconColorRes()));

        // --- Set dữ liệu cho ImageView ---

        // 1. Lấy drawable nền tròn gốc
        Drawable backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.icon_circle_background).mutate();

        // 2. Ép kiểu nó về GradientDrawable để có thể đổi màu
        if (backgroundDrawable instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) backgroundDrawable;
            // 3. Đặt màu mới cho nó
            gradientDrawable.setColor(ContextCompat.getColor(context, voucher.getIconBackgroundColorRes()));
        }

        // 4. Gán drawable đã được tô màu làm background cho ImageView
        holder.ivIcon.setBackground(backgroundDrawable);

        // 5. Đặt hình ảnh và tô màu cho icon (phần này giữ nguyên)
        holder.ivIcon.setImageResource(voucher.getIconRes());
        ColorStateList iconColor = ColorStateList.valueOf(ContextCompat.getColor(context, voucher.getTitleAndIconColorRes()));
        ImageViewCompat.setImageTintList(holder.ivIcon, iconColor);
    }
    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    // ViewHolder không thay đổi
    public static class VoucherViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc;
        ImageView ivIcon;
        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_voucher_title);
            tvDesc = itemView.findViewById(R.id.tv_voucher_desc);
            ivIcon = itemView.findViewById(R.id.iv_voucher_icon);
        }
    }
}