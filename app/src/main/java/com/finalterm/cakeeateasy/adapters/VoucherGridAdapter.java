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
    private OnVoucherClickListener listener;

    public interface OnVoucherClickListener {
        void onVoucherClick(Voucher voucher);
    }

    // SỬA LẠI CONSTRUCTOR ĐỂ NHẬN LISTENER
    public VoucherGridAdapter(Context context, List<Voucher> voucherList, OnVoucherClickListener listener) {
        this.context = context;
        this.voucherList = voucherList;
        this.listener = listener;
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

        // Giả sử model Voucher có các hàm get này
        holder.tvTitle.setText(voucher.getTitle());
        holder.tvDesc.setText(voucher.getDescription());
        holder.tvTitle.setTextColor(ContextCompat.getColor(context, voucher.getTitleAndIconColorRes()));

        // --- Logic đổi màu background và icon ---
        Drawable background = ContextCompat.getDrawable(context, R.drawable.icon_circle_background);
        if (background != null) {
            // Dùng mutate() để thay đổi không ảnh hưởng đến các drawable khác
            Drawable mutatedBackground = background.mutate();
            if (mutatedBackground instanceof GradientDrawable) {
                GradientDrawable gradientDrawable = (GradientDrawable) mutatedBackground;
                gradientDrawable.setColor(ContextCompat.getColor(context, voucher.getIconBackgroundColorRes()));
                holder.ivIcon.setBackground(gradientDrawable);
            }
        }

        holder.ivIcon.setImageResource(voucher.getIconRes());
        ColorStateList iconColor = ColorStateList.valueOf(ContextCompat.getColor(context, voucher.getTitleAndIconColorRes()));
        ImageViewCompat.setImageTintList(holder.ivIcon, iconColor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onVoucherClick(voucher);
            }
        });
    }
    @Override
    public int getItemCount() {
        return voucherList.size();
    }

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