package com.example.instagramcloneapp.model;

import android.graphics.Bitmap;

public class UserPost {
    /**
     * Stores the username
     */
    private String username;
    /**
     * Stores bitmap of the post of current user
     */
    private Bitmap bitmap;

    /**
     * Constructor initialises username and bitmap
     * @param username
     * @param bitmap
     */
    public UserPost(String username, Bitmap bitmap){
        this.username = username;
        this.bitmap = bitmap;
    }

    /**
     * @return username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @return bitmap of the current user post
     */
    public Bitmap getBitmap() {
        return bitmap;
    }
}
