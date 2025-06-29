package com.finalterm.cakeeateasy.screens.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.ChatAdapter;
import com.finalterm.cakeeateasy.models.ChatMessage;
import com.google.android.material.appbar.MaterialToolbar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatDialogFragment extends DialogFragment {

    private RecyclerView rvChatMessages;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;
    private MaterialToolbar toolbar;
    private EditText edtChatMessage;
    private ImageView btnSendMessage;

    public ChatDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.AppTheme_FullScreenDialog);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_chat, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Khởi tạo các View ---
        toolbar = view.findViewById(R.id.toolbar_chat);
        rvChatMessages = view.findViewById(R.id.rv_chat_messages);
        edtChatMessage = view.findViewById(R.id.edt_chat_message);
        btnSendMessage = view.findViewById(R.id.btn_send_message);

        // --- Thiết lập Toolbar ---
        toolbar.setNavigationOnClickListener(v -> dismiss());

        // --- Thiết lập RecyclerView ---
        setupRecyclerView();

        // --- Tải dữ liệu ---
        loadChatMessages();

        // --- Gán sự kiện cho nút Gửi ---
        btnSendMessage.setOnClickListener(v -> sendMessage());
    }

    private void setupRecyclerView() {
        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), messageList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true);
        rvChatMessages.setLayoutManager(layoutManager);
        rvChatMessages.setAdapter(chatAdapter);
    }

    private void sendMessage() {
        String messageText = edtChatMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            // Lấy thời gian hiện tại
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String currentTime = sdf.format(new Date());

            // Tạo đối tượng tin nhắn mới
            ChatMessage newMessage = new ChatMessage(messageText, currentTime, true);

            // Thêm vào danh sách và cập nhật RecyclerView
            messageList.add(newMessage);
            chatAdapter.notifyItemInserted(messageList.size() - 1);

            // Cuộn xuống tin nhắn mới nhất
            rvChatMessages.scrollToPosition(messageList.size() - 1);

            // Xóa nội dung trong ô nhập liệu
            edtChatMessage.setText("");
        }
    }

    private void loadChatMessages() {
        // ... (code tạo dữ liệu giả giữ nguyên) ...
        messageList.add(new ChatMessage("Hey there!", "10:10", false));
        messageList.add(new ChatMessage("This is your delivery driver...", "10:10", false));
        messageList.add(new ChatMessage("Hi!", "10:10", true));
        chatAdapter.notifyDataSetChanged();
    }
}