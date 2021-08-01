package com.example.instagramcloneapp.controller;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.views.UserProfileViewHolder;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class UserProfileAdapter extends RecyclerView.Adapter<UserProfileViewHolder> {

    /**
     * Stores an arraylist of bitmaps
     */
    public ArrayList<Bitmap> bitmapArrayList;

    /**
     * Initialises arraylist of bitmaps
     * @param bitmapArrayList
     */
    public UserProfileAdapter(ArrayList<Bitmap> bitmapArrayList){
        this.bitmapArrayList = bitmapArrayList;
    }
    @NonNull
    @NotNull
    @Override
    public UserProfileViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.personal_post_layout, parent, false);
        return new UserProfileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UserProfileViewHolder holder, int position) {
        holder.getUserProfilePostsImageView().setImageBitmap(bitmapArrayList.get(position));
    }

    /**
     * @return number of bitmaps
     */
    @Override
    public int getItemCount() {
        return bitmapArrayList.size();
    }
}
