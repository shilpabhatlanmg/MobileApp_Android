package com.protectapp.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;


public class ChatModel {

    private String chatMessage;
    private int senderID;
    private String senderName;
    private long timestamp;
    public ChatModel()
    {

    }
    public ChatModel(int senderID, String senderName, String chatMessage) {
        this.chatMessage = chatMessage;
        this.senderID = senderID;
        this.senderName = senderName;
        }

    public String getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(String chatMessage) {
        this.chatMessage = chatMessage;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public java.util.Map<String, String> getTimestamp() {
        return ServerValue.TIMESTAMP;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    @Exclude
    public long getTimeStampLong() {
        return timestamp;
    }
}
