package com.finalterm.cakeeateasy.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Product implements Parcelable {

    private int productId;
    private String name;
    private String description;
    private double price;
    private double originalPrice;
    private int stock;
    private int categoryId;
    private String imageUrl;
    private String categoryName;

    /**
     * === CONSTRUCTOR DUY NHẤT VÀ ĐẦY ĐỦ NHẤT ===
     * Dùng cho việc tạo đối tượng từ Database.
     * @param productId ID của sản phẩm
     * @param name Tên sản phẩm
     * @param description Mô tả
     * @param price Giá bán
     * @param originalPrice Giá gốc
     * @param stock Tồn kho
     * @param categoryId ID của danh mục
     * @param imageUrl URL ảnh
     * @param categoryName Tên danh mục (lấy từ JOIN)
     */
    public Product(int productId, String name, String description, double price, double originalPrice, int stock, int categoryId, String imageUrl, String categoryName) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.originalPrice = originalPrice;
        this.stock = stock;
        this.categoryId = categoryId;
        this.imageUrl = imageUrl;
        this.categoryName = categoryName;
    }

    // --- Getters and Setters ---
    // Đổi tên getId() thành getProductId() cho nhất quán
    public int getProductId() { return productId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public int getStock() { return stock; }
    public int getCategoryId() { return categoryId; }
    public String getImageUrl() { return imageUrl; }
    public String getCategoryName() { return categoryName; }

    // Các setter không cần thiết vì ta dùng constructor, nhưng giữ lại nếu bạn cần
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public void setStock(int stock) { this.stock = stock; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }


    // --- Parcelable Implementation (Đã cập nhật để bao gồm categoryName) ---
    protected Product(Parcel in) {
        productId = in.readInt();
        name = in.readString();
        description = in.readString();
        price = in.readDouble();
        originalPrice = in.readDouble();
        stock = in.readInt();
        categoryId = in.readInt();
        imageUrl = in.readString();
        categoryName = in.readString(); // THÊM DÒNG NÀY
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(productId);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeDouble(price);
        dest.writeDouble(originalPrice);
        dest.writeInt(stock);
        dest.writeInt(categoryId);
        dest.writeString(imageUrl);
        dest.writeString(categoryName); // THÊM DÒNG NÀY
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };
}