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
import com.finalterm.cakeeateasy.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private Context context;
    private List<Notification> notificationList;
    // SỬA LỖI 1: Khai báo biến để lưu listener
    private OnNotificationClickListener clickListener;

    // SỬA LỖI 1 & 3: Thêm interface OnNotificationClickListener
    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification, int position);
    }

    // SỬA LỖI 1 & 3: Cập nhật constructor để nhận listener
    public NotificationAdapter(Context context, List<Notification> notificationList, OnNotificationClickListener listener) {
        this.context = context;
        this.notificationList = notificationList;
        this.clickListener = listener;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notificationList.get(position);

        holder.txtTitle.setText(notification.getTitle());
        holder.txtSubtitle.setText(notification.getSubtitle());
        holder.txtTime.setText(notification.getTime());
        holder.txtDate.setText(notification.getDate());

        holder.viewUnreadDot.setVisibility(notification.isRead() ? View.GONE : View.VISIBLE);

        int iconRes;
        int iconColorRes;
        int iconBgColorRes;

        switch (notification.getType()) {
            case SUCCESS:
                iconRes = R.drawable.ic_order_success;
                iconColorRes = R.color.notif_status_success;
                iconBgColorRes = R.color.notif_status_success_bg;
                break;
            case ERROR:
                iconRes = R.drawable.ic_cancel;
                iconColorRes = R.color.notif_status_error;
                iconBgColorRes = R.color.notif_status_error_bg;
                break;
            case ACCOUNT:
                iconRes = R.drawable.ic_person_outline;
                iconColorRes = R.color.notif_status_success;
                iconBgColorRes = R.color.notif_status_success_bg;
                break;
            case PROMO:
            default:
                iconRes = R.drawable.ic_discount;
                iconColorRes = R.color.notif_status_promo;
                iconBgColorRes = R.color.notif_status_promo_bg;
                break;
        }

        Drawable backgroundDrawable = ContextCompat.getDrawable(context, R.drawable.icon_circle_background).mutate();
        if (backgroundDrawable instanceof GradientDrawable) {
            ((GradientDrawable) backgroundDrawable).setColor(ContextCompat.getColor(context, iconBgColorRes));
        }
        holder.imgIcon.setBackground(backgroundDrawable);

        holder.imgIcon.setImageResource(iconRes);
        ColorStateList iconColor = ColorStateList.valueOf(ContextCompat.getColor(context, iconColorRes));
        ImageViewCompat.setImageTintList(holder.imgIcon, iconColor);
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    // Sửa ViewHolder để xử lý click
    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView txtTitle, txtSubtitle, txtTime, txtDate;
        View viewUnreadDot;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_notif_icon);
            txtTitle = itemView.findViewById(R.id.txt_notif_title);
            txtSubtitle = itemView.findViewById(R.id.txt_notif_subtitle);
            txtTime = itemView.findViewById(R.id.txt_notif_time);
            txtDate = itemView.findViewById(R.id.txt_notif_date);
            viewUnreadDot = itemView.findViewById(R.id.view_unread_dot);

            // SỬA LỖI 1: Gán sự kiện click cho toàn bộ item
            itemView.setOnClickListener(v -> {
                if (clickListener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    clickListener.onNotificationClick(notificationList.get(getAdapterPosition()), getAdapterPosition());
                }
            });
        }
    }
}