package com.protectapp.model;

import java.io.Serializable;
import java.util.Objects;

public class Contact implements Serializable {
    private int userID;
    private String name;
    private String lastMessage;
    private String profileImageURL;
    private String timestampOfLastMessage;
    private int unreadMessages;
    private String nodeIdentifier;
    private String contactNumber;

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(String profileImageURL) {
        this.profileImageURL = profileImageURL;
    }

    public String getTimestampOfLastMessage() {
        return timestampOfLastMessage;
    }

    public void setTimestampOfLastMessage(String timestampOfLastMessage) {
        this.timestampOfLastMessage = timestampOfLastMessage;
    }

    public int getUnreadMessages() {
        return unreadMessages;
    }

    public void setUnreadMessages(int unreadMessages) {
        this.unreadMessages = unreadMessages;
    }

    public String getNodeIdentifier() {
        return nodeIdentifier;
    }

    public void setNodeIdentifier(String nodeIdentifier) {
        this.nodeIdentifier = nodeIdentifier;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        return userID == contact.userID;
    }

    @Override
    public int hashCode() {

        return Objects.hash(userID);
    }
}
