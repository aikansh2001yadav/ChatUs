package com.example.instagramcloneapp.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.model.UserPost;
import com.example.instagramcloneapp.views.UserPostViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostViewHolder> {
    /**
     * Stores the arraylist of user posts
     */
    private ArrayList<UserPost> userPostArrayList;

    /**
     * Initialises arraylist of user posts
     * @param userPostArrayList
     */
    public UserPostAdapter(ArrayList<UserPost> userPostArrayList) {
        this.userPostArrayList = userPostArrayList;
    }

    @NonNull
    @NotNull
    @Override
    public UserPostViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_layout, parent, false);
        return new UserPostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserPostViewHolder holder, int position) {
        holder.getPostUserNameTextView().setText(userPostArrayList.get(position).getUsername());
        holder.getUserPostImageView().setImageBitmap(userPostArrayList.get(position).getBitmap());
    }

    /**
     * @return the number of posts to be shown
     */
    @Override
    public int getItemCount() {
        return userPostArrayList.size();
    }
}
