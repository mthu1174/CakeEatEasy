package com.finalterm.cakeeateasy.screens.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.finalterm.cakeeateasy.databinding.FragmentFilterBinding; // Quan trọng: Thay đổi nếu package của bạn khác
import com.google.android.material.chip.Chip;

/**
 * Fragment để hiển thị các tùy chọn lọc sản phẩm.
 */
public class FilterFragment extends Fragment {

    // Khai báo biến View Binding. Nó sẽ thay thế cho việc dùng findViewById.
    private FragmentFilterBinding binding;

    public FilterFragment() {
        // Required empty public constructor
    }

    // Chúng ta không cần newInstance và các tham số vì không có dữ liệu đầu vào.
    // Nếu sau này bạn cần truyền dữ liệu vào Fragment này, bạn có thể thêm lại.

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Phần này có thể để trống vì chúng ta không nhận tham số.
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Sử dụng View Binding để "thổi phồng" (inflate) layout.
        // Cách này an toàn và hiệu quả hơn inflater.inflate() truyền thống.
        binding = FragmentFilterBinding.inflate(inflater, container, false);

        // Trả về view gốc của layout đã được binding.
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Sau khi view đã được tạo, chúng ta bắt đầu cài đặt các listener và logic.
        setupToolbar();
        setupPriceMilestones();
        setupSaveButton();
    }

    private void setupToolbar() {
        // Thiết lập sự kiện click cho nút back trên toolbar
        binding.toolbarFilter.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lấy Activity đang chứa Fragment này và gọi hàm onBackPressed() của nó.
                // Hành động này giống hệt như khi người dùng bấm nút back của hệ thống.
                if (getActivity() != null) {
                    getActivity().onBackPressed();
                }
            }
        });
    }

    private void setupPriceMilestones() {
        // Thiết lập sự kiện click cho từng mốc giá
        // Khi bấm vào, nó sẽ cập nhật giá trị của Slider
        binding.txtPriceMilestone1.setOnClickListener(v -> binding.sliderPrice.setValue(100f));
        binding.txtPriceMilestone2.setOnClickListener(v -> binding.sliderPrice.setValue(300f));
        binding.txtPriceMilestone3.setOnClickListener(v -> binding.sliderPrice.setValue(600f));
        binding.txtPriceMilestone4.setOnClickListener(v -> binding.sliderPrice.setValue(900f));
    }

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            // 1. Lấy giá trị từ slider
            float selectedPrice = binding.sliderPrice.getValue();

            // 2. Lấy category đã chọn
            int selectedCategoryId = binding.chipGroupCategory.getCheckedChipId();
            String selectedCategoryText = "None"; // Giá trị mặc định nếu không có gì được chọn
            if (selectedCategoryId != View.NO_ID) {
                // Tìm Chip bằng ID và lấy text của nó
                selectedCategoryText = ((Chip) binding.getRoot().findViewById(selectedCategoryId)).getText().toString();
            }

            // 3. Lấy size đã chọn
            int selectedSizeId = binding.chipGroupSize.getCheckedChipId();
            String selectedSizeText = "None"; // Giá trị mặc định
            if (selectedSizeId != View.NO_ID) {
                selectedSizeText = ((Chip) binding.getRoot().findViewById(selectedSizeId)).getText().toString();
            }

            // In ra Logcat để kiểm tra kết quả
            String logMessage = "Price: " + selectedPrice + ", Category: " + selectedCategoryText + ", Size: " + selectedSizeText;
            Log.d("FilterResult", logMessage);

            // TODO: Gửi dữ liệu filter về màn hình trước đó (dùng Fragment Result API hoặc ViewModel)

            // Cuối cùng, quay lại màn hình trước
        });
    }

    /**
     * Rất quan trọng: Phải giải phóng biến binding trong onDestroyView
     * để tránh rò rỉ bộ nhớ (memory leak) khi Fragment bị hủy.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}