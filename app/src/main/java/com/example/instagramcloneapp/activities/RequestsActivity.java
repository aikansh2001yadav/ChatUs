package com.example.instagramcloneapp.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.controller.ChatStatusAdapter;
import com.example.instagramcloneapp.model.ChatStatus;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class RequestsActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    /**
     * Stores a reference of progressBar to show progress
     */
    private ProgressBar progressBar;
    /**
     * Custom adapter to be set to chatStatusRecyclerView
     */
    private ChatStatusAdapter chatStatusAdapter;
    /**
     * Stores an arraylist of various chatStatus requests
     */
    private ArrayList<ChatStatus> chatStatusArrayList;
    /**
     * Stores a reference of chatStatusRecyclerView to show various requests
     */
    private RecyclerView chatStatusRecyclerView;
    /**
     * Stores a reference of swipeRefreshLayout
     */
    private SwipeRefreshLayout requestSwipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        //Initialising various views and setting chatStatusAdapter to chatStatusRecyclerView
        chatStatusRecyclerView = findViewById(R.id.chatStatusRecyclerView);

        requestSwipeRefreshLayout = findViewById(R.id.requestSwipeRefreshLayout);
        requestSwipeRefreshLayout.setOnRefreshListener(this);
        chatStatusArrayList = new ArrayList<>();

        progressBar = findViewById(R.id.requestProgressBar);
        chatStatusAdapter = new ChatStatusAdapter(this, chatStatusArrayList);
        chatStatusRecyclerView.setAdapter(chatStatusAdapter);
        chatStatusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Showing progress bar
        progressBar.setVisibility(View.VISIBLE);
        //Storing various chatStatusList
        getChatStatusList();
    }

    public void getChatStatusList(){
        //Refreshing chatStatusArrayList from parse server
        ParseQuery<ParseObject> query = ParseQuery.getQuery("ChatStatus");
        query.whereEqualTo("Recipient", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    chatStatusArrayList.clear();
                    for(ParseObject parseObject:objects){
                        String sender = parseObject.getString("Sender");
                        boolean chatStatus = parseObject.getBoolean("ChatStatus");
                        chatStatusArrayList.add(new ChatStatus(sender, chatStatus));
                    }
                    chatStatusAdapter.notifyDataSetChanged();
                }else{
                    showAlert("Error", "Something went wrong: " + e.getMessage());
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Showing alert
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        new AlertDialog.Builder(RequestsActivity.this)
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
        //Getting chat lists
        getChatStatusList();
        requestSwipeRefreshLayout.setRefreshing(false);
    }
}