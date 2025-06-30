package com.finalterm.cakeeateasy.screens.dialogs;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.finalterm.cakeeateasy.databinding.FragmentFilterBinding;
import com.google.android.material.chip.Chip;

import java.util.List;

/**
 * Fragment để hiển thị các tùy chọn lọc sản phẩm.
 * Phiên bản đã được đơn giản hóa, loại bỏ logic cho các mốc giá.
 */
public class FilterFragment extends Fragment {

    // Khai báo biến View Binding
    private FragmentFilterBinding binding;

    public FilterFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFilterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Chỉ cần cài đặt Toolbar và nút Save
        setupToolbar();
        setupSaveButton();
    }

    private void setupToolbar() {
        binding.toolbarFilter.setNavigationOnClickListener(v -> {
            // Dùng NavController để quay lại màn hình trước.
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    // ===================================================================
    // HÀM setupPriceMilestones() ĐÃ ĐƯỢC XÓA BỎ HOÀN TOÀN
    // ===================================================================

    private void setupSaveButton() {
        binding.btnSave.setOnClickListener(v -> {
            // 1. Lấy giá trị từ RangeSlider
            List<Float> priceValues = binding.sliderPrice.getValues();
            float minPrice = priceValues.get(0);
            float maxPrice = priceValues.get(1);

            // 2. Lấy category đã chọn
            int selectedCategoryId = binding.chipGroupCategory.getCheckedChipId();
            String selectedCategoryText = "None";
            if (selectedCategoryId != View.NO_ID) {
                Chip selectedChip = binding.chipGroupCategory.findViewById(selectedCategoryId);
                if (selectedChip != null) {
                    selectedCategoryText = selectedChip.getText().toString();
                }
            }

            // 3. Lấy size đã chọn
            int selectedSizeId = binding.chipGroupSize.getCheckedChipId();
            String selectedSizeText = "None";
            if (selectedSizeId != View.NO_ID) {
                Chip selectedChip = binding.chipGroupSize.findViewById(selectedSizeId);
                if (selectedChip != null) {
                    selectedSizeText = selectedChip.getText().toString();
                }
            }

            // In ra Logcat để kiểm tra kết quả
            String logMessage = "Filter Applied -> Price Range: " + minPrice + " - " + maxPrice
                    + ", Category: " + selectedCategoryText
                    + ", Size: " + selectedSizeText;
            Log.d("FilterResult", logMessage);

            // TODO: Gửi dữ liệu filter về màn hình trước đó

            // Quay lại màn hình trước
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}