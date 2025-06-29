package com.finalterm.cakeeateasy.screens; // Thay bằng package của bạn

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.finalterm.cakeeateasy.R; // Thay bằng package của bạn
import com.finalterm.cakeeateasy.screens.dialogs.ReviewSuccessDialogFragment; // Thay bằng package của bạn

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText edtReviewText;
    private View btnSendReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        // Tham chiếu đến các view cần dùng
        ratingBar = findViewById(R.id.rating_bar);
        edtReviewText = findViewById(R.id.edt_review_text);
        btnSendReview = findViewById(R.id.btn_send_review);

        // Gán sự kiện cho nút back trên toolbar
        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        // GÁN SỰ KIỆN CHO NÚT SEND REVIEW
        btnSendReview.setOnClickListener(v -> {
            // Gọi hàm xử lý logic khi nhấn nút
            handleSendReview();
        });

        // Bạn có thể giữ lại các hàm setup nút upload ở đây nếu cần
        // setupUploadButtons();
    }

    private void handleSendReview() {
        // Lấy dữ liệu từ các view
        float ratingValue = ratingBar.getRating();
        String reviewText = edtReviewText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (ratingValue == 0) {
            Toast.makeText(this, "Please provide a rating.", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu chưa có rating
        }

        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Please write your review.", Toast.LENGTH_SHORT).show();
            return; // Dừng lại nếu chưa có text
        }

        // Nếu mọi thứ hợp lệ, hiển thị dialog
        showSuccessDialog();
    }

    private void showSuccessDialog() {
        // Tạo một instance của DialogFragment
        ReviewSuccessDialogFragment dialogFragment = new ReviewSuccessDialogFragment();

        // Hiển thị dialog
        // Dùng getSupportFragmentManager() vì chúng ta đang ở trong một AppCompatActivity
        // "success_dialog" là một tag định danh duy nhất cho dialog này
        dialogFragment.show(getSupportFragmentManager(), "success_dialog");
    }
}