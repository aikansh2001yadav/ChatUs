package com.example.instagramcloneapp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagramcloneapp.ChatActivity;
import com.example.instagramcloneapp.MainActivity;
import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.controller.UsersAdapter;
import com.example.instagramcloneapp.model.User;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public UsersFragment() {
        //empty constructor
    }

    /**
     * Stores a reference of friendSwipeRefreshLayout
     */
    private SwipeRefreshLayout friendSwipeRefreshLayout;
    /**
     * Stores a list of friends of the current user
     */
    private ArrayList<String> friendsList;
    /**
     * Stores a list of users
     */
    private ArrayList<User> usersList;
    private UsersAdapter usersAdapter;
    private ProgressBar usersProgressBar;
    private RecyclerView usersRecyclerView;

    public UsersFragment(ArrayList<String> friendsList) {
        this.friendsList = friendsList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_friends, container, false);
        //Initialising views
        usersRecyclerView = view.findViewById(R.id.usersRecyclerView);
        usersProgressBar = view.findViewById(R.id.usersProgressBar);
        friendSwipeRefreshLayout = view.findViewById(R.id.friendSwipeRefreshLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        friendSwipeRefreshLayout.setOnRefreshListener(this);
        usersList = new ArrayList<>();
        usersAdapter = new UsersAdapter(getContext(), usersList, friendsList);
        usersRecyclerView.setAdapter(usersAdapter);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }


    @Override
    public void onResume() {
        super.onResume();
        getUsersList();
    }

    /**
     * Getting lists of users from parse server and setting the usersAdapter to the recyclerview
     */
    private void getUsersList() {
        usersProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseUsers, ParseException e) {
                if(e == null){
                    usersList.clear();
                    if(!parseUsers.isEmpty()){
                        for(ParseUser parseUser: parseUsers) {
                            String username = parseUser.getUsername();
                            if (!username.equals(ParseUser.getCurrentUser().getUsername())) {
                                if(friendsList.contains(username)){
                                    usersList.add(new User(username, true));
                                }else {
                                    usersList.add(new User(username, false));
                                }
                            }
                        }
//                        usersAdapter = new UsersAdapter(getContext(), usersList, friendsList);
//                        usersRecyclerView.setAdapter(usersAdapter);
                        usersAdapter.notifyDataSetChanged();
                    }else{
                        showAlert("No Users found", "No able to find users");
                    }
                }else{
                    showAlert("Error", "Something went wrong: " + e.getMessage());
                }
                usersProgressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Showing alert
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(getContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setCancelable(false)
                .show();
    }

    @Override
    public void onRefresh() {
        getUsersList();
        friendSwipeRefreshLayout.setRefreshing(false);
    }
}