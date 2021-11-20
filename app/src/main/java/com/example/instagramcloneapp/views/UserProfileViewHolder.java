package com.example.instagramcloneapp.views;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;

import org.jetbrains.annotations.NotNull;

public class UserProfileViewHolder extends RecyclerView.ViewHolder {
    /**
     * Stores a reference of userProfilePostsImageView used to show post of the current user
     */
    private ImageView userProfilePostsImageView;

    /**
     * Constructor initialises userProfilePostsImageView
     * @param itemView
     */
    public UserProfileViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        userProfilePostsImageView = itemView.findViewById(R.id.user_personal_post_layout);
    }

    /**
     * @return userProfilePostsImageView
     */
    public ImageView getUserProfilePostsImageView() {
        return userProfilePostsImageView;
    }
}
