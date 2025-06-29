package com.finalterm.cakeeateasy.models; // Thay bằng package của bạn

public class ChatMessage {
    private String text;
    private String timestamp;
    private boolean sentByMe;

    public ChatMessage(String text, String timestamp, boolean sentByMe) {
        this.text = text;
        this.timestamp = timestamp;
        this.sentByMe = sentByMe;
    }

    public String getText() {
        return text;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public boolean isSentByMe() {
        return sentByMe;
    }
}