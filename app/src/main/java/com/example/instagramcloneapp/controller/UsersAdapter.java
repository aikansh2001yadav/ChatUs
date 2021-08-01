package com.example.instagramcloneapp.controller;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.ChatActivity;
import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.UserProfileDetailsActivity;
import com.example.instagramcloneapp.model.User;
import com.example.instagramcloneapp.views.UsersViewHolder;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.GREEN;

public class UsersAdapter extends RecyclerView.Adapter<UsersViewHolder> {
    /**
     * Stores context of main activity
     */
    private Context context;
    /**
     * Stores an arraylist of users
     */
    private ArrayList<User> usersList;
    /**
     * Stores an arraylist of friends
     */
    private ArrayList<String> friendsList;

    /**
     * Initialises context, usersList and friendsList arraylist
     * @param context
     * @param usersList
     * @param friendsList
     */
    public UsersAdapter(Context context, ArrayList<User> usersList, ArrayList<String> friendsList) {
        this.context = context;
        this.usersList = usersList;
        this.friendsList = friendsList;
    }

    @NonNull
    @NotNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false);
        return new UsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull UsersViewHolder holder, int position) {
        //Setting name of the user on textview
        holder.getUserNameTextView().setText(usersList.get(holder.getAdapterPosition()).getUsername());
        //Changing color and name of text whether the user is friend or not accordingly
        if(usersList.get(holder.getAdapterPosition()).isFriend()){
            holder.getFollowButton().setBackgroundColor(GREEN);
            holder.getFollowButton().setText("FOLLOWED");
        }
        //Setting on click listener on the chat button
        holder.getChatButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatStatus");
                query.whereEqualTo("Sender", ParseUser.getCurrentUser().getUsername());
                query.whereEqualTo("Recipient", usersList.get(holder.getAdapterPosition()).getUsername());
                query.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {
                        if(e == null){
                            if(!objects.isEmpty()){
                                for(ParseObject parseObject: objects){
                                    if(parseObject.getBoolean("ChatStatus")){
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        intent.putExtra("Receiver", usersList.get(holder.getAdapterPosition()).getUsername());
                                        context.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(context, "You are not allowed to talk to " + usersList.get(holder.getAdapterPosition()).getUsername(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            }else{
                                ParseObject friendStatus = new ParseObject("ChatStatus");
                                friendStatus.put("Sender", ParseUser.getCurrentUser().getUsername());
                                friendStatus.put("Recipient", usersList.get(holder.getAdapterPosition()).getUsername());
                                friendStatus.put("ChatStatus", false);
                                try {
                                    friendStatus.save();
                                    Toast.makeText(context, "Request has been sent to talk. Please ask your friend to accept request", Toast.LENGTH_LONG).show();
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                            }
                        }else{
                            Toast.makeText(context, "Something went wrong: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        //Setting on click listener on the follow button
        holder.getFollowButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*If user is not friend, then follow user clicked and change color and text accordingly
                but if user is friend then unfollow user clicked and change color and text accordingly
                 */
                if(!usersList.get(holder.getAdapterPosition()).isFriend()) {
                    followFriend(holder.getAdapterPosition());
                    friendsList.add(usersList.get(holder.getAdapterPosition()).getUsername());
                    holder.getFollowButton().setBackgroundColor(GREEN);
                    holder.getFollowButton().setText("Followed");
                    usersList.get(holder.getAdapterPosition()).setIsFriend(true);
                }else{
                    unfollowFriend(holder.getAdapterPosition());
                    friendsList.remove(usersList.get(holder.getAdapterPosition()).getUsername());
                    holder.getFollowButton().setBackgroundColor(Color.rgb(170, 46, 230));
                    holder.getFollowButton().setText("Follow");
                    usersList.get(holder.getAdapterPosition()).setIsFriend(false);
                }
            }
        });
        //Starting userProfileDetails activity when somebody clicks on the name of the user
        holder.getUserNameTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileDetailsActivity.class);
                intent.putExtra("username", usersList.get(holder.getAdapterPosition()).getUsername());
                context.startActivity(intent);
            }
        });
    }

    /**
     * @return number of users
     */
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    /**
     * Adds user to friends' list to the current parse user
     * @param position
     */
    public void followFriend(int position){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            parseObject.add("friendsList", usersList.get(position).getUsername());
                            try {
                                parseObject.save();
                            } catch (ParseException parseException) {
                                parseException.printStackTrace();
                            }
                        }
                    } else {
                        ParseObject friends = new ParseObject("Friends");
                        friends.put("username", ParseUser.getCurrentUser().getUsername());
                        friends.add("friendsList", usersList.get(position).getUsername());
                        try {
                            friends.save();
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                    }
                } else {
                    Toast.makeText(context, "Something went wrong" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * Remove user from friends' list from the current parse user
     * @param position
     */
    public void unfollowFriend(int position){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    for(ParseObject parseObject:objects){
                        ArrayList<String> currentUserNameList = (ArrayList<String>) parseObject.get("friendsList");
                        currentUserNameList.remove(usersList.get(position).getUsername());
                        parseObject.remove("friendsList");
                        parseObject.addAll("friendsList", currentUserNameList);
                        try {
                            parseObject.save();
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                    }
                }else{
                    Toast.makeText(context, "Something went wrong" + e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
