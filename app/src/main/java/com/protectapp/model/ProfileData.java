package com.protectapp.model;

import java.io.Serializable;

public class ProfileData implements Serializable {


    private int userID;

    private String contactNumber;

    private String profileImageURL;

    private String token;

    private boolean shouldCreatePassword;

    private String emailAddress;

    private String name;

    public String getName ()
    {
        return name;
    }

    public void setName (String name)
    {
        this.name = name;
    }

    public int getUserID ()
    {
        return userID;
    }

    public void setUserID (int userID)
    {
        this.userID = userID;
    }

    public String getContactNumber ()
    {
        return contactNumber;
    }

    public void setContactNumber (String contactNumber)
    {
        this.contactNumber = contactNumber;
    }

    public String getProfileImageURL ()
    {
        return profileImageURL;
    }

    public void setProfileImageURL (String profileImageURL)
    {
        this.profileImageURL = profileImageURL;
    }

    public String getToken ()
    {
        return token;
    }

    public void setToken (String token)
    {
        this.token = token;
    }

    public boolean isShouldCreatePassword() {
        return shouldCreatePassword;
    }

    public void setShouldCreatePassword(boolean shouldCreatePassword) {
        this.shouldCreatePassword = shouldCreatePassword;
    }

    public String getEmailAddress ()
    {
        return emailAddress;
    }

    public void setEmailAddress (String emailAddress)
    {
        this.emailAddress = emailAddress;
    }

}
