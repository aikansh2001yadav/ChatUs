package com.example.instagramcloneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.instagramcloneapp.controller.CustomAdapter;
import com.example.instagramcloneapp.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /**
     * Stores a reference of tabLayout
     */
    private TabLayout tabLayout;
    /**
     * Stores a reference of viewPager
     */
    private ViewPager2 viewPager;
    /**
     * Stores an arrayList of friendsList
     */
    private ArrayList<String> friendsList;
    private CustomAdapter customAdapter;
    /**
     * Stores a reference of logoutProgressbar which is shown when te user is logged out
     */
    private ProgressBar logoutProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
        friendsList = new ArrayList<>();
        getFriendsList();
        customAdapter = new CustomAdapter(this, friendsList);
        viewPager.setAdapter(customAdapter);

        //Setting the text of each tab position
        new TabLayoutMediator(tabLayout, viewPager, true, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NotNull TabLayout.Tab tab, int position) {
                // position of the current tab and that tab
                switch(position){
                    case 0:{
                        tab.setText("User Profile");
                        break;
                    }
                    case 1:{
                        tab.setText("Friends");
                        break;
                    }
                    default:{
                        tab.setText("User Posts");
                        break;
                    }
                }
            }
        }).attach();

        logoutProgressBar = findViewById(R.id.logoutProgressBar);
        logoutProgressBar.setVisibility(View.GONE);

        String username = getIntent().getStringExtra("username");
        //Setting username of the current user to the title
        setTitle(username);
        //Adjusting touch sensitivity
        try {
            final Field recyclerViewField = ViewPager2.class.getDeclaredField("mRecyclerView");
            recyclerViewField.setAccessible(true);

            final RecyclerView recyclerView = (RecyclerView) recyclerViewField.get(viewPager);

            final Field touchSlopField = RecyclerView.class.getDeclaredField("mTouchSlop");
            touchSlopField.setAccessible(true);

            final int touchSlop = (int) touchSlopField.get(recyclerView);
            touchSlopField.set(recyclerView, touchSlop * 4);//6 is empirical value
        } catch (Exception ignore) {
        }
    }

    /**
     * Showing alert
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        logoutProgressBar.setVisibility(View.GONE);
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.logoutButton){
            logoutProgressBar.setVisibility(View.VISIBLE);
            //Logging out the current user when the logout item is selected
            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {
                    if(e == null){
                        showAlert("Logging out", "Current user has been successfully logged out");
                    }
                }
            });
            return true;
        }else if(item.getItemId() == R.id.requestButton){
            Intent intent = new Intent(MainActivity.this, RequestsActivity.class);
            startActivity(intent);
            return true;
        } else{
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Getting list of friends from the parse server and adding them to list of friends
     */
    private void getFriendsList() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Friends");
        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    friendsList.clear();
                    if(!objects.isEmpty()){
                        for(ParseObject parseObject:objects){
                            List<String> currentFriendsList = parseObject.getList("friendsList");
                            if(currentFriendsList!= null){
                                friendsList.addAll(currentFriendsList);
                            }
                        }
                    }
                }else{
                    showAlert("Error", "Something went wrong: " + e.getMessage());
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        moveTaskToBack(true);
        finish();
    }
}