package com.finalterm.cakeeateasy.screens.dialogs;

import android.content.DialogInterface; // <-- THÊM IMPORT
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.finalterm.cakeeateasy.R;

public class ReviewSuccessDialogFragment extends DialogFragment {

    // === BƯỚC 1: THÊM CƠ CHẾ LISTENER ===
    public interface OnDismissListener {
        void onDialogDismissed();
    }

    private OnDismissListener dismissListener;

    public void setOnDismissListener(OnDismissListener listener) {
        this.dismissListener = listener;
    }
    // =====================================

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_review_success, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Đơn giản hóa: Cả hai nút đều chỉ có một nhiệm vụ là đóng dialog.
        view.findViewById(R.id.btn_close_dialog).setOnClickListener(v -> dismiss());
        view.findViewById(R.id.btn_dialog_ok).setOnClickListener(v -> dismiss());

        // BỎ logic getActivity().finish() ở đây đi, vì nó sẽ được xử lý qua listener.
    }

    // === BƯỚC 2: GỌI LISTENER KHI DIALOG BỊ ĐÓNG ===
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDialogDismissed();
        }
    }
    // =============================================
}