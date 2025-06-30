package com.finalterm.cakeeateasy.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;
import java.util.UUID;

/**
 * Model class for a Category.
 * Đã được nâng cấp để hỗ trợ Parcelable (truyền dữ liệu giữa các màn hình),
 * có một ID duy nhất và các phương thức equals/hashCode/toString.
 */
public class Category implements Parcelable {

    private String id; // ID duy nhất cho mỗi category
    private String name;
    private int imageResId; // Đổi tên để rõ ràng hơn, đây là ID của resource

    // Constructor để tạo đối tượng mới với ID ngẫu nhiên
    public Category(String name, int imageResId) {
        this.id = UUID.randomUUID().toString(); // Tự động tạo một ID duy nhất
        this.name = name;
        this.imageResId = imageResId;
    }

    // Constructor để tạo đối tượng với ID cụ thể (hữu ích khi lấy từ DB/API)
    public Category(String id, String name, int imageResId) {
        this.id = id;
        this.name = name;
        this.imageResId = imageResId;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }

    // --- Triển khai Parcelable để truyền đối tượng qua Intent ---

    protected Category(Parcel in) {
        id = in.readString();
        name = in.readString();
        imageResId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(imageResId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Category> CREATOR = new Creator<Category>() {
        @Override
        public Category createFromParcel(Parcel in) {
            return new Category(in);
        }

        @Override
        public Category[] newArray(int size) {
            return new Category[size];
        }
    };

    // --- Các phương thức tiêu chuẩn cho model object ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id); // So sánh dựa trên ID duy nhất
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Dựa trên ID duy nhất
    }

    @NonNull
    @Override
    public String toString() {
        return "Category{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}