package com.finalterm.cakeeateasy.screens.dialogs; // Thay bằng package của bạn

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.finalterm.cakeeateasy.R; // Thay bằng package của bạn

public class ReviewSuccessDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Chỉ inflate layout ở đây
        return inflater.inflate(R.layout.dialog_review_success, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        // --- DI CHUYỂN LOGIC CẤU HÌNH WINDOW VÀO onStart() ---
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Đặt kích thước cho dialog
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // Làm cho nền của dialog trong suốt để thấy được góc bo tròn từ background.xml
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // --- Chỉ giữ lại logic gán sự kiện click ở đây ---
        view.findViewById(R.id.btn_close_dialog).setOnClickListener(v -> dismiss());

        view.findViewById(R.id.btn_dialog_ok).setOnClickListener(v -> {
            // Đóng dialog
            dismiss();
            // Sau khi nhấn OK, đóng cả ReviewActivity
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }
}