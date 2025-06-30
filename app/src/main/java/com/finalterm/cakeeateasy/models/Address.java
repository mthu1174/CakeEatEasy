package com.finalterm.cakeeateasy.models;

import java.io.Serializable;

public class Address implements Serializable {
    private int id;
    private String name;
    private String phone;
    private String addressLine;
    private boolean isDefault;

    public Address(int id, String name, String phone, String addressLine, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.addressLine = addressLine;
        this.isDefault = isDefault;
    }

    public Address() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
} 