package com.finalterm.cakeeateasy.screens; // Hoặc package của bạn

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity; // THÊM IMPORT NÀY
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Nullable;
import com.finalterm.cakeeateasy.R; // Thay bằng package của bạn

public class ProfileOptionView extends LinearLayout {

    private ImageView imgIcon;
    private TextView txtName;

    public ProfileOptionView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        // "Bơm" layout view_profile_option.xml vào LinearLayout này
        LayoutInflater.from(context).inflate(R.layout.view_profile_option, this, true);

        // Đặt các thuộc tính mặc định cho LinearLayout
        setOrientation(LinearLayout.HORIZONTAL); // SỬA Ở ĐÂY
        setGravity(Gravity.CENTER_VERTICAL);    // VÀ SỬA Ở ĐÂY

        int padding = (int) (16 * getResources().getDisplayMetrics().density);
        setPadding(padding, padding, padding, padding);

        // Tham chiếu đến các view con
        imgIcon = findViewById(R.id.img_option_icon);
        txtName = findViewById(R.id.txt_option_name);

        // Đặt background ở đây để đảm bảo nó được áp dụng
        setBackgroundResource(R.drawable.profile_card_background);
    }

    // Các phương thức public để Activity có thể set dữ liệu
    public void setOptionIcon(int drawableRes) {
        if (imgIcon != null) {
            imgIcon.setImageResource(drawableRes);
        }
    }

    public void setOptionName(String name) {
        if (txtName != null) {
            txtName.setText(name);
        }
    }
}