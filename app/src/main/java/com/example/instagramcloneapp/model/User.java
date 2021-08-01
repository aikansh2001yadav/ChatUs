package com.example.instagramcloneapp.model;

public class User {
    /**
     * Stores the username
     */
    private String username;
    /**
     * Stores whether someone is friend or not
     */
    private boolean isFriend;

    /**
     * Constructor initialises username and isFriend
     * @param username
     * @param isFriend
     */
    public User(String username, boolean isFriend){
        this.username = username;
        this.isFriend = isFriend;
    }

    /**
     * Returns the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return whether user is friend or not
     */
    public boolean isFriend() {
        return isFriend;
    }

    /**
     * Sets whether user is friend or not
     * @param isFriend
     */
    public void setIsFriend(boolean isFriend){
        this.isFriend = isFriend;
    }
}
