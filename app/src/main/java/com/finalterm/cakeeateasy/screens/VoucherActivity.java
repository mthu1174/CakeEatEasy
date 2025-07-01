package com.finalterm.cakeeateasy.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.finalterm.cakeeateasy.R;
import com.finalterm.cakeeateasy.adapters.VoucherGridAdapter; // Giả định bạn dùng adapter này
import com.finalterm.cakeeateasy.db.DatabaseHelper;
import com.finalterm.cakeeateasy.models.Voucher; // Giả định bạn dùng model này

import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình hiển thị danh sách các voucher.
 * Cho phép người dùng chọn một voucher hợp lệ để áp dụng.
 */
public class VoucherActivity extends AppCompatActivity implements VoucherGridAdapter.OnVoucherClickListener {

    // --- UI Components ---
    private RecyclerView rvAvailable, rvUnavailable;
    private TextView tvAvailableEmpty, tvUnavailableEmpty;

    // --- Adapters and Data ---
    private VoucherGridAdapter availableAdapter, unavailableAdapter;
    private List<Voucher> availableList;
    private List<Voucher> unavailableList;

    // --- Helper ---
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voucher);

        // 1. Khởi tạo các thành phần phụ thuộc (như Database)
        dbHelper = new DatabaseHelper(this);

        // 2. Ánh xạ các View từ layout
        initViews();

        // 3. Cấu hình RecyclerView và các Adapter
        setupRecyclerViews();

        // 4. Tải dữ liệu, phân loại và hiển thị lên UI
        loadAndClassifyVouchers();
    }

    /**
     * Ánh xạ các thành phần giao diện từ file XML.
     */
    private void initViews() {
        rvAvailable = findViewById(R.id.rv_available_vouchers);
        rvUnavailable = findViewById(R.id.rv_unavailable_vouchers);
        tvAvailableEmpty = findViewById(R.id.tv_available_empty);
        tvUnavailableEmpty = findViewById(R.id.tv_unavailable_empty);
    }

    /**
     * Khởi tạo RecyclerView, LayoutManager và Adapter cho cả hai danh sách.
     */
    private void setupRecyclerViews() {
        availableList = new ArrayList<>();
        unavailableList = new ArrayList<>();

        // Cấu hình cho danh sách voucher CÓ THỂ DÙNG
        rvAvailable.setLayoutManager(new LinearLayoutManager(this));
        // Adapter này cần `this` (Activity) làm listener để xử lý click
        availableAdapter = new VoucherGridAdapter(this, availableList, this);
        rvAvailable.setAdapter(availableAdapter);

        // Cấu hình cho danh sách voucher KHÔNG THỂ DÙNG
        rvUnavailable.setLayoutManager(new LinearLayoutManager(this));
        // Adapter này không cần listener, vì voucher không hợp lệ không nên được chọn.
        // Truyền 'null' để vô hiệu hóa sự kiện click.
        unavailableAdapter = new VoucherGridAdapter(this, unavailableList, null);
        rvUnavailable.setAdapter(unavailableAdapter);
    }

    /**
     * Tải tất cả voucher từ cơ sở dữ liệu, sau đó phân loại chúng
     * vào danh sách "dùng được" và "không dùng được".
     */
    private void loadAndClassifyVouchers() {
        // Bước 1: Lấy toàn bộ voucher từ CSDL
        List<Voucher> allVouchersFromDb = dbHelper.getAllVouchers();

        // Xóa dữ liệu cũ để tránh trùng lặp nếu hàm này được gọi lại
        availableList.clear();
        unavailableList.clear();

        // TODO: Lấy tổng tiền của đơn hàng hiện tại từ Intent.
        // Đây là bước quan trọng để có logic phân loại chính xác.
        // Ví dụ: double currentOrderTotal = getIntent().getDoubleExtra("ORDER_TOTAL", 0.0);

        // Bước 2: Lặp qua và phân loại từng voucher
        for (Voucher voucher : allVouchersFromDb) {
            // --- LOGIC PHÂN LOẠI THỰC TẾ NÊN ĐẶT TẠI ĐÂY ---
            // Ví dụ logic thực tế:
            // boolean isApplicable = voucher.getMinimumOrderAmount() <= currentOrderTotal && voucher.isNotExpired();

            // Logic tạm thời để minh họa (giống trong code gốc của bạn):
            boolean isApplicable = !("SAVE15".equals(voucher.getVoucherCode()) || "SAVE20".equals(voucher.getVoucherCode()));

            // Gán trạng thái cho voucher để adapter có thể hiển thị đúng (VD: màu xám/màu)
            voucher.setAvailable(isApplicable);

            // Thêm voucher vào danh sách tương ứng
            if (isApplicable) {
                availableList.add(voucher);
            } else {
                unavailableList.add(voucher);
            }
        }

        // Bước 3: Thông báo cho adapter rằng dữ liệu đã thay đổi
        availableAdapter.notifyDataSetChanged();
        unavailableAdapter.notifyDataSetChanged();

        // Bước 4: Kiểm tra và hiển thị/ẩn thông báo nếu danh sách rỗng
        checkEmptyStates();
    }

    /**
     * Kiểm tra xem các danh sách có rỗng không và hiển thị/ẩn thông báo tương ứng.
     * Giúp giao diện thân thiện hơn với người dùng.
     */
    private void checkEmptyStates() {
        tvAvailableEmpty.setVisibility(availableList.isEmpty() ? View.VISIBLE : View.GONE);
        rvAvailable.setVisibility(availableList.isEmpty() ? View.GONE : View.VISIBLE);

        tvUnavailableEmpty.setVisibility(unavailableList.isEmpty() ? View.VISIBLE : View.GONE);
        rvUnavailable.setVisibility(unavailableList.isEmpty() ? View.GONE : View.VISIBLE);
    }

    /**
     * Callback được gọi khi một voucher trong danh sách "Có thể dùng" được nhấn.
     * @param voucher Voucher đã được chọn.
     */
    @Override
    public void onVoucherClick(Voucher voucher) {
        // Tạo Intent để chứa dữ liệu trả về
        Intent resultIntent = new Intent();

        // Đóng gói thông tin của voucher được chọn
        resultIntent.putExtra("voucher_code", voucher.getVoucherCode());
        resultIntent.putExtra("voucher_amount", voucher.getDiscountAmount());
        // Bạn có thể thêm các thông tin khác nếu cần
        // resultIntent.putExtra("voucher_type", voucher.getType());

        // Đặt kết quả là OK và đính kèm dữ liệu
        setResult(RESULT_OK, resultIntent);

        // Đóng Activity này và quay lại màn hình trước đó
        finish();
    }
}