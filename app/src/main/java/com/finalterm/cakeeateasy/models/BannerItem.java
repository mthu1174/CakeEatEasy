package com.finalterm.cakeeateasy.models;

public class BannerItem {
    private final int imageResId;
    private final String title;
    private final String buttonText;

    public BannerItem(int imageResId, String title, String buttonText) {
        this.imageResId = imageResId;
        this.title = title;
        this.buttonText = buttonText;
    }

    public int getImageResId() { return imageResId; }
    public String getTitle() { return title; }
    public String getButtonText() { return buttonText; }
}
