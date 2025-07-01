package com.finalterm.cakeeateasy.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Category implements Parcelable {

    private int categoryId;
    private String name;
    private String image; // URL hoặc đường dẫn file ảnh

    /**
     * Constructor dùng để tạo đối tượng mới (trước khi insert vào DB, vì ID sẽ tự tăng).
     */
    public Category(String name, String image) {
        this.name = name;
        this.image = image;
    }

    /**
     * Constructor dùng để đọc đối tượng từ Database (đã có ID).
     */
    public Category(int categoryId, String name, String image) {
        this.categoryId = categoryId;
        this.name = name;
        this.image = image;
    }

    // --- Getters ---
    public int getCategoryId() { return categoryId; }
    public String getName() { return name; }
    public String getImage() { return image; }

    // --- Parcelable Implementation ---
    protected Category(Parcel in) {
        categoryId = in.readInt();
        name = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(categoryId);
        dest.writeString(name);
        dest.writeString(image);
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
}