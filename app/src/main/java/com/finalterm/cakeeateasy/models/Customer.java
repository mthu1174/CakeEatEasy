package com.finalterm.cakeeateasy.models;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;

public class Customer implements Parcelable {

    private int customerId;
    private String name;
    private String email;
    private String password;
    private String phone;
    private String address;
    private String dob;
    private  String username;// Date of Birth

    /**
     * Constructor mặc định.
     */
    public Customer() {
    }

    /**
     * SỬA LỖI: Constructor dùng khi đăng ký.
     * Đã được sửa lại để nhận vào 6 tham số String, khớp với lời gọi từ SignUpActivity.
     */
    public Customer(String name, String username, String email, String password, String phone, String dob, String address) {
        this.name = name;
        this.username = username; // Thêm username
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.dob = dob;
        this.address = address;
    }

    /**
     * Constructor đầy đủ nhất, dùng để đọc từ Database.
     */
    public Customer(int customerId, String name, String email, String password, String phone, String address, String dob) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.dob = dob;
    }

    // --- Getters and Setters ---
    public int getCustomerId() { return customerId; }
    public void setCustomerId(int customerId) { this.customerId = customerId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getDob() { return dob; }
    public void setDob(String dob) { this.dob = dob; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }


    // --- Parcelable Implementation ---
    protected Customer(Parcel in) {
        customerId = in.readInt();
        name = in.readString();
        email = in.readString();
        password = in.readString();
        phone = in.readString();
        address = in.readString();
        dob = in.readString();
        username = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(customerId);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(dob);
        dest.writeString(username);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };
}