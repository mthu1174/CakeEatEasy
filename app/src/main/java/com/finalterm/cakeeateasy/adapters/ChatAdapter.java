package com.finalterm.cakeeateasy.adapters; // Thay bằng package của bạn

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.finalterm.cakeeateasy.R; // Thay bằng package của bạn
import com.finalterm.cakeeateasy.models.ChatMessage; // Thay bằng package của bạn

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // Định danh cho 2 loại view (tin nhắn gửi và nhận)
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;

    private Context context;
    private List<ChatMessage> messageList;

    /**
     * Constructor để nhận dữ liệu từ Activity/Fragment.
     */
    public ChatAdapter(Context context, List<ChatMessage> messageList) {
        this.context = context;
        this.messageList = messageList;
    }

    /**
     * Trả về loại view tại một vị trí cụ thể.
     * RecyclerView sẽ dùng thông tin này trong onCreateViewHolder.
     */
    @Override
    public int getItemViewType(int position) {
        if (messageList.get(position).isSentByMe()) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    /**
     * Tạo ViewHolder tương ứng với từng loại view.
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_sent, parent, false);
            return new SentMessageViewHolder(view);
        } else { // viewType == VIEW_TYPE_RECEIVED
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_received, parent, false);
            return new ReceivedMessageViewHolder(view);
        }
    }

    /**
     * Gán dữ liệu từ đối tượng ChatMessage vào các View trong ViewHolder.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder.getItemViewType() == VIEW_TYPE_SENT) {
            ((SentMessageViewHolder) holder).bind(message);
        } else {
            ((ReceivedMessageViewHolder) holder).bind(message);
        }
    }

    /**
     * Trả về tổng số lượng item trong danh sách.
     */
    @Override
    public int getItemCount() {
        return messageList.size();
    }


    // ==========================================================
    // CÁC LỚP VIEWHOLDER BÊN TRONG
    // ==========================================================

    /**
     * ViewHolder cho tin nhắn gửi đi (bên phải, màu hồng).
     */
    private static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessageBody, txtMessageTime;
        ImageView imgMessageStatus;

        public SentMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageBody = itemView.findViewById(R.id.txt_message_body);
            txtMessageTime = itemView.findViewById(R.id.txt_message_time);
            imgMessageStatus = itemView.findViewById(R.id.img_message_status);
        }

        void bind(ChatMessage message) {
            txtMessageBody.setText(message.getText());
            txtMessageTime.setText(message.getTimestamp());
        }
    }

    /**
     * ViewHolder cho tin nhắn nhận được (bên trái, màu trắng).
     */
    private static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessageBody, txtMessageTime;

        public ReceivedMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMessageBody = itemView.findViewById(R.id.txt_message_body);
            txtMessageTime = itemView.findViewById(R.id.txt_message_time);
        }

        void bind(ChatMessage message) {
            txtMessageBody.setText(message.getText());
            txtMessageTime.setText(message.getTimestamp());
        }
    }
}