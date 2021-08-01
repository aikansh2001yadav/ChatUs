package com.example.instagramcloneapp.views;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;

import org.jetbrains.annotations.NotNull;


public class UserPostViewHolder extends RecyclerView.ViewHolder {
    /**
     * Storing reference of postUserNameTextView used to show username
     */
    private TextView postUserNameTextView;
    /**
     * Storing reference of userPostImageView used to show posts
     */
    private ImageView userPostImageView;

    /**
     * Constructor initialises postUserNameTextView and userPostImageView
     * @param itemView
     */
    public UserPostViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        postUserNameTextView = itemView.findViewById(R.id.postUserNameTextView);
        userPostImageView = itemView.findViewById(R.id.userProfilePostsImageView);
    }

    /**
     * @return userPostImageView
     */
    public ImageView getUserPostImageView() {
        return userPostImageView;
    }

    /**
     * @return postUserNameTextView
     */
    public TextView getPostUserNameTextView() {
        return postUserNameTextView;
    }
}
