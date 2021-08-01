package com.example.instagramcloneapp.views;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;

import org.jetbrains.annotations.NotNull;

public class UsersViewHolder extends RecyclerView.ViewHolder{
    /**
     * Stores a reference of userNameTextView used to show username
     */
    private TextView userNameTextView;
    /**
     * Stores a reference of chatButton used to start chatActivity
     */
    private Button chatButton;
    /**
     * Stores a reference of followButton used to follow someBody
     */
    private Button followButton;

    /**
     * Constructor initialises userNameTextView, chatButton and followButton
     * @param itemView
     */
    public UsersViewHolder(@NonNull @NotNull View itemView) {
        super(itemView);
        userNameTextView = itemView.findViewById(R.id.userNameTextView);
        chatButton = itemView.findViewById(R.id.chatButton);
        followButton = itemView.findViewById(R.id.followButton);
    }

    /**
     * @return userNameTextView
     */
    public TextView getUserNameTextView() {
        return userNameTextView;
    }

    /**
     * @return chatButton
     */
    public Button getChatButton(){
        return chatButton;
    }

    /**
     * @return followButton
     */
    public Button getFollowButton() {
        return followButton;
    }
}
