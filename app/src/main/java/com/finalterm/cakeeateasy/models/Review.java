package com.finalterm.cakeeateasy.models;

public class Review {
    private String userName;
    private float rating;
    private String comment;
    private String date;
    private String userAvatarUrl; // URL ảnh đại diện của người dùng

    public Review(String userName, float rating, String comment, String date, String userAvatarUrl) {
        this.userName = userName;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
        this.userAvatarUrl = userAvatarUrl;
    }

    // Getters
    public String getUserName() { return userName; }
    public float getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
    public String getUserAvatarUrl() { return userAvatarUrl; }
}