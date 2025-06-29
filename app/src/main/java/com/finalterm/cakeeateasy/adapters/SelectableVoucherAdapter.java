package com.finalterm.cakeeateasy.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.models.SelectableVoucher;

import java.util.List;

public class SelectableVoucherAdapter extends RecyclerView.Adapter<SelectableVoucherAdapter.VoucherViewHolder> {

    private Context context;
    private List<SelectableVoucher> voucherList;
    private int lastSelectedPosition = -1; // Vị trí của item được chọn cuối cùng

    public SelectableVoucherAdapter(Context context, List<SelectableVoucher> voucherList) {
        this.context = context;
        this.voucherList = voucherList;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.component_voucher, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        SelectableVoucher voucher = voucherList.get(position);

        holder.txtTitle.setText(voucher.getTitle());
        holder.txtValidity.setText(voucher.getValidity());
        holder.imgIcon.setImageResource(voucher.getIconRes());
        holder.rbSelect.setChecked(voucher.isSelected());

        // Xử lý giao diện active/inactive
        if (voucher.isAvailable()) {
            holder.itemView.setBackgroundResource(R.drawable.coupon_background_active);
            holder.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.home_pink)); // Màu hồng
            // ... set các màu khác cho trạng thái active
        } else {
            holder.itemView.setBackgroundResource(R.drawable.coupon_background_inactive);
            holder.txtTitle.setTextColor(ContextCompat.getColor(context, R.color.home_text_secondary)); // Màu xám
            // ... set các màu khác cho trạng thái inactive
        }

        // Chỉ cho phép chọn nếu voucher có sẵn
        holder.rbSelect.setEnabled(voucher.isAvailable());
    }

    @Override
    public int getItemCount() {
        return voucherList.size();
    }

    class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtValidity;
        RadioButton rbSelect;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.iv_coupon_icon);
            txtTitle = itemView.findViewById(R.id.tv_coupon_title);
            txtValidity = itemView.findViewById(R.id.tv_coupon_validity);
            rbSelect = itemView.findViewById(R.id.rb_coupon_select);

            // Xử lý logic chọn một RadioButton duy nhất
            rbSelect.setOnClickListener(v -> {
                int copyOfLastSelectedPosition = lastSelectedPosition;
                lastSelectedPosition = getAdapterPosition();

                // Bỏ chọn item cũ
                if (copyOfLastSelectedPosition != -1) {
                    voucherList.get(copyOfLastSelectedPosition).setSelected(false);
                    notifyItemChanged(copyOfLastSelectedPosition);
                }
                // Chọn item mới
                voucherList.get(lastSelectedPosition).setSelected(true);
                notifyItemChanged(lastSelectedPosition);

                // TODO: Thông báo cho Activity biết voucher nào đã được chọn
            });
        }
    }
}