package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.screens.dialogs.ReviewSuccessDialogFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class ReviewActivity extends AppCompatActivity {

    private static final String TAG = "ReviewActivity";

    // --- UI Elements ---
    private MaterialToolbar toolbar;
    private RatingBar ratingBar;
    private EditText edtReviewText;
    private AppCompatButton btnSendReview;

    // UI cho phần upload ảnh
    private View btnUploadImage;
    private FrameLayout layoutMediaPreview;
    private ImageView imgPreview;
    private View btnClearMedia;

    // --- Logic ---
    private Uri selectedImageUri = null; // Để lưu lại đường dẫn ảnh đã chọn

    // Trình xử lý kết quả khi người dùng chọn ảnh từ thư viện
    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    Log.d(TAG, "Ảnh đã được chọn: " + uri.toString());
                    selectedImageUri = uri;
                    showMediaPreview(uri);
                } else {
                    Log.d(TAG, "Không có ảnh nào được chọn.");
                }
            });

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        initViews();
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        ratingBar = findViewById(R.id.rating_bar);
        edtReviewText = findViewById(R.id.edt_review_text);
        btnSendReview = findViewById(R.id.btn_send_review);

        // Ánh xạ các view cho phần upload ảnh
        btnUploadImage = findViewById(R.id.btn_upload_image);
        layoutMediaPreview = findViewById(R.id.layout_media_preview);
        imgPreview = findViewById(R.id.img_preview);
        btnClearMedia = findViewById(R.id.btn_clear_media);
    }

    private void setupListeners() {
        // Nút back trên toolbar
        toolbar.setNavigationOnClickListener(v -> finish());

        // Nút "Send Review"
        btnSendReview.setOnClickListener(v -> handleSendReview());

        // Nút "Upload Image"
        btnUploadImage.setOnClickListener(v -> {
            // Mở thư viện ảnh để người dùng chọn
            imagePickerLauncher.launch("image/*");
        });

        // Nút "X" để xóa ảnh đã chọn
        btnClearMedia.setOnClickListener(v -> {
            hideMediaPreview();
        });
    }

    private void handleSendReview() {
        float ratingValue = ratingBar.getRating();
        String reviewText = edtReviewText.getText().toString().trim();

        // Kiểm tra dữ liệu đầu vào
        if (ratingValue == 0) {
            Toast.makeText(this, "Vui lòng chọn số sao đánh giá.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (reviewText.isEmpty()) {
            Toast.makeText(this, "Vui lòng viết nhận xét của bạn.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mô phỏng việc gửi review (chỉ log ra, không làm gì phức tạp)
        Log.d(TAG, "Đánh giá: " + ratingValue + " sao, Nội dung: " + reviewText);
        if (selectedImageUri != null) {
            Log.d(TAG, "Kèm ảnh: " + selectedImageUri.toString());
        } else {
            Log.d(TAG, "Không có ảnh đính kèm.");
        }

        // Hiển thị dialog thành công mà không cần lưu vào DB
        showSuccessDialog();
    }

    // Hàm để hiển thị phần preview ảnh
    private void showMediaPreview(Uri imageUri) {
        imgPreview.setImageURI(imageUri);
        layoutMediaPreview.setVisibility(View.VISIBLE);
    }

    // Hàm để ẩn phần preview và reset lựa chọn
    private void hideMediaPreview() {
        imgPreview.setImageURI(null); // Xóa ảnh
        layoutMediaPreview.setVisibility(View.GONE);
        selectedImageUri = null; // Reset biến lưu URI
    }

    private void showSuccessDialog() {
        ReviewSuccessDialogFragment dialogFragment = new ReviewSuccessDialogFragment();
        // Lambda expression để khi dialog đóng thì activity này cũng đóng theo
        dialogFragment.setOnDismissListener(this::finish);
        dialogFragment.show(getSupportFragmentManager(), "success_dialog");
    }
}
//package com.finalterm.cakeeateasy.screens;
//
//import android.content.Context;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.widget.EditText;
//import android.widget.RatingBar;
//import android.widget.Toast;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.finalterm.cakeeateasy.R;
//import com.finalterm.cakeeateasy.db.DatabaseHelper; // Import DatabaseHelper
//import com.finalterm.cakeeateasy.screens.dialogs.ReviewSuccessDialogFragment;
//
//public class ReviewActivity extends AppCompatActivity {
//
//    // BƯỚC 1: Thêm các key để nhận dữ liệu từ Intent
//    public static final String EXTRA_PRODUCT_ID = "product_id_to_review";
//    public static final String EXTRA_ORDER_ID = "order_id_to_review";
//
//    // --- UI Elements ---
//    private RatingBar ratingBar;
//    private EditText edtReviewText;
//    private View btnSendReview;
//
//    // --- Data & Logic ---
//    private DatabaseHelper dbHelper;
//    private int currentUserId = -1;
//    private int productIdToReview = -1;
//    private String orderIdToReview = null; // Order ID có thể là String
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_review);
//
//        dbHelper = new DatabaseHelper(this);
//
//        // Lấy dữ liệu từ Intent và SharedPreferences
//        loadDataFromIntentAndPrefs();
//
//        // Kiểm tra dữ liệu đầu vào. Nếu không hợp lệ, không cho phép review.
//        if (currentUserId == -1 || productIdToReview == -1 || orderIdToReview == null) {
//            Toast.makeText(this, "Lỗi: Không đủ thông tin để đánh giá.", Toast.LENGTH_LONG).show();
//            finish();
//            return;
//        }
//
//        initViews();
//        setupListeners();
//    }
//
//    private void initViews() {
//        ratingBar = findViewById(R.id.rating_bar);
//        edtReviewText = findViewById(R.id.edt_review_text);
//        btnSendReview = findViewById(R.id.btn_send_review);
//    }
//
//    private void loadDataFromIntentAndPrefs() {
//        // Lấy Product ID và Order ID từ Intent
//        Intent intent = getIntent();
//        productIdToReview = intent.getIntExtra(EXTRA_PRODUCT_ID, -1);
//        orderIdToReview = intent.getStringExtra(EXTRA_ORDER_ID);
//
//        // BƯỚC 2: Lấy User ID từ SharedPreferences
//        SharedPreferences prefs = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
//        currentUserId = prefs.getInt("userId", -1);
//    }
//
//    private void setupListeners() {
//        findViewById(R.id.toolbar).setOnClickListener(v -> finish());
//        btnSendReview.setOnClickListener(v -> handleSendReview());
//    }
//
//    private void handleSendReview() {
//        float ratingValue = ratingBar.getRating();
//        String reviewText = edtReviewText.getText().toString().trim();
//
//        // Kiểm tra dữ liệu đầu vào như cũ
//        if (ratingValue == 0) {
//            Toast.makeText(this, "Vui lòng chọn số sao đánh giá.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        if (reviewText.isEmpty()) {
//            Toast.makeText(this, "Vui lòng viết nhận xét của bạn.", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        // BƯỚC 3: Tích hợp Database
//        // Gọi hàm để thêm review vào DB
//        boolean isSuccess = dbHelper.addReview(
//                currentUserId,
//                productIdToReview,
//                orderIdToReview,
//                ratingValue,
//                reviewText
//        );
//
//        // BƯỚC 4: Cải thiện luồng xử lý
//        if (isSuccess) {
//            // Chỉ hiển thị dialog khi đã lưu thành công
//            showSuccessDialog();
//        } else {
//            Toast.makeText(this, "Gửi đánh giá thất bại. Vui lòng thử lại.", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void showSuccessDialog() {
//        ReviewSuccessDialogFragment dialogFragment = new ReviewSuccessDialogFragment();
//
//        // Tùy chọn: Implement một listener để biết khi nào dialog bị đóng
//        dialogFragment.setOnDismissListener(() -> {
//            // Sau khi người dùng đóng dialog thành công, đóng luôn ReviewActivity
//            finish();
//        });
//
//        dialogFragment.show(getSupportFragmentManager(), "success_dialog");
//    }
//}