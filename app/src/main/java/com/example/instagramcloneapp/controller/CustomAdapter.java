package com.example.instagramcloneapp.controller;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.instagramcloneapp.fragments.UsersFragment;
import com.example.instagramcloneapp.fragments.UserPostsFragment;
import com.example.instagramcloneapp.fragments.UserProfileFragment;

import java.util.ArrayList;

public class CustomAdapter extends FragmentStateAdapter {
    /**
     * Stores an arraylist of list of friends of the current user
     */
    private ArrayList<String> friendsList;

    /**
     * Constructor initialises friendsList
     * @param fragmentActivity
     * @param friendsList
     */
    public CustomAdapter(@NonNull @org.jetbrains.annotations.NotNull FragmentActivity fragmentActivity, ArrayList<String> friendsList) {
        super(fragmentActivity);
        this.friendsList = friendsList;
    }

    /**
     * @param position
     * @return fragment on the basis of tab's position selected
     */
    @NonNull
    @org.jetbrains.annotations.NotNull
    @Override
    public Fragment createFragment(int position) {
        switch(position){
            case 0:{
                return new UserProfileFragment();
            }
            case 1:{
                return new UsersFragment(friendsList);
            }
            case 2:{
                return new UserPostsFragment(friendsList);
            }
            default:{
                return null;
            }
        }
    }

    /**
     * @return the number of tabs
     */
    @Override
    public int getItemCount() {
        return 3;
    }
}
