package com.example.instagramcloneapp.activities;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.controller.UserProfileAdapter;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileDetailsActivity extends AppCompatActivity {

    /**
     * Stores the username
     */
    private String username;
    /**
     * Stores an arraylist of bitmap
     */
    private ArrayList<Bitmap> bitmapArrayList;
    /**
     * Stores a reference of userProfileAdapter
     */
    private UserProfileAdapter userProfileAdapter;
    /**
     * Stores a reference of recyclerView
     */
    private RecyclerView userProfileRecyclerView;
    /**
     * Stores a reference of CircleImageView used to show user profile imageView
     */
    private CircleImageView userProfilePictureImageView;
    /**
     * Stores a reference of userProfileNmeTextView used to show current user's name
     */
    private TextView userProfileNameTextView;
    /**
     * Stores a reference of userProfileAgeTextView used to show current user's age
     */
    private TextView userProfileAgeTextView;
    /**
     * Stores a reference of userProfileGenderTextView used to show current user's gender
     */
    private TextView userProfileGenderTextView;
    /**
     * Stores a reference of userProfileBioTextView used to show current user's bio
     */
    private TextView userProfileBioTextView;
    /**
     * Stores a reference of linearLayout which shows posts
     */
    private LinearLayout postsLayout;
    /**
     * Stores a reference of progressBar
     */
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_details);
        username = getIntent().getStringExtra("username");
        bitmapArrayList = new ArrayList<>();

        //Setting title of the current activity
        setTitle("Profile Details");

        //Initialising views
        userProfileNameTextView = findViewById(R.id.userProfileNameTextView);
        userProfileAgeTextView = findViewById(R.id.userProfileAgeTextView);
        userProfileGenderTextView = findViewById(R.id.userProfileGenderTextView);
        userProfileBioTextView = findViewById(R.id.userProfileBioTextView);
        userProfilePictureImageView = findViewById(R.id.userProfileImageView);
        postsLayout = findViewById((R.id.recyclerview_posts_layout));
        progressBar = findViewById(R.id.profile_progress);
//        progressLinearLayout = findViewById(R.id.progressLinearLayout);

        //Initialising userProfileRecyclerView and userProfileAdapter
        userProfileRecyclerView = findViewById(R.id.userProfileRecyclerView);
        userProfileAdapter = new UserProfileAdapter(bitmapArrayList);
        userProfileRecyclerView.setAdapter(userProfileAdapter);
        userProfileRecyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.VISIBLE);
        postsLayout.setVisibility(View.GONE);
        getPosts();
        updateProfileDetails();
    }

    /**
     * Updating userProfileDetails
     */
    private void updateProfileDetails() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
        query.whereEqualTo("Username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(!objects.isEmpty()){
                        for(ParseObject parseObject:objects){
                            String name = parseObject.getString("Name");
                            String lastName = parseObject.getString("LastName");
                            if(name != null && lastName != null) {
                                userProfileNameTextView.setText(parseObject.getString("Name") + " " + parseObject.getString("LastName"));
                            }else{
                                userProfileNameTextView.setText("No Info found");
                            }

                            String age = parseObject.getString("Age");
                            if(age != null) {
                                userProfileAgeTextView.setText(parseObject.getString("Age") + " years");
                            }else{
                                userProfileAgeTextView.setText("No Info found");
                            }

                            String gender = parseObject.getString("Gender");
                            if(gender != null) {
                                userProfileGenderTextView.setText(parseObject.getString("Gender"));
                            }else{
                                userProfileGenderTextView.setText("No Info found");
                            }

                            String bio = parseObject.getString("Bio");
                            if(bio != null) {
                                userProfileBioTextView.setText(parseObject.getString("Bio"));
                            }else{
                                userProfileBioTextView.setText("No Info found");
                            }

                            ParseFile parseFile = parseObject.getParseFile("ProfilePicture");
                            if(parseFile != null) {
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(parseFile.getData(), 0, parseFile.getData().length);
                                    userProfilePictureImageView.setImageBitmap(bitmap);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                            }
                        }
                    }else{
                        userProfileNameTextView.setText("No Info found");
                        userProfileAgeTextView.setText("No Info found");
                        userProfileGenderTextView.setText("No Info found");
                        userProfileBioTextView.setText("No Info found");
                    }
                }else{
                    showAlert("Error", "Something went wrong: " + e.getMessage());
                }
            }
        });
    }

    /**
     * Getting posts from parse server and adding to the arraylist of bitmaps of posts by the user
     */
    public void getPosts(){
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPosts");
        query.whereEqualTo("username", username);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    bitmapArrayList.clear();
                    if(!objects.isEmpty()){
                        for(ParseObject parseObject:objects){
                            List<ParseFile> userPosts = parseObject.getList("userPosts");
                            if(userPosts != null){
                                for(ParseFile parseFile:userPosts){
                                    if(parseFile != null){
                                        try {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(parseFile.getData(), 0, parseFile.getData().length);
                                            bitmapArrayList.add(bitmap);
                                        } catch (ParseException parseException) {
                                            parseException.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        postsLayout.setVisibility(View.VISIBLE);
                        userProfileAdapter = new UserProfileAdapter(bitmapArrayList);
                        userProfileRecyclerView.setAdapter(userProfileAdapter);
                        userProfileAdapter.notifyDataSetChanged();
                    } else {
                        postsLayout.setVisibility(View.GONE);
                    }
                } else {
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
        new AlertDialog.Builder(UserProfileDetailsActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getPosts();
                    }
                })
                .setCancelable(false)
                .show();
    }
}