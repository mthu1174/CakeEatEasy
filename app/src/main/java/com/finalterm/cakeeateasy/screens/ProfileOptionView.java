package com.finalterm.cakeeateasy.screens;

import static android.view.Gravity.CENTER_VERTICAL;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.finalterm.cakeeateasy.R;

public class ProfileOptionView extends LinearLayout {

    // Khai báo các view bên trong
    private ImageView iconView;
    private TextView nameView;

    // Các constructor bắt buộc
    public ProfileOptionView(Context context) {
        super(context);
        init(context);
    }

    public ProfileOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ProfileOptionView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_profile_option, this, true);

        setOrientation(HORIZONTAL);
        setGravity(CENTER_VERTICAL);
        // Thiết lập layout params để nó chiếm toàn bộ chiều rộng
        setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        // === SỬA LỖI TẠI ĐÂY: DÙNG ĐÚNG ID TỪ FILE XML CỦA BẠN ===
        iconView = findViewById(R.id.img_option_icon); // Đúng ID: img_option_icon
        nameView = findViewById(R.id.txt_option_name); // Đúng ID: txt_option_name
    }

    /**
     * Phương thức public để cài đặt icon cho option.
     * @param resId ID của tài nguyên drawable (ví dụ: R.drawable.ic_security)
     */
    public void setOptionIcon(int resId) {
        if (iconView != null) {
            iconView.setImageResource(resId);
        }
    }

    /**
     * Phương thức public để cài đặt tên cho option.
     * @param name Chuỗi văn bản để hiển thị (ví dụ: "Bảo mật")
     */
    public void setOptionName(String name) {
        if (nameView != null) {
            nameView.setText(name);
        }
    }
}