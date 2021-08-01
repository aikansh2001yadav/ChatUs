package com.example.instagramcloneapp.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagramcloneapp.R;
import com.example.instagramcloneapp.controller.UserPostAdapter;
import com.example.instagramcloneapp.model.UserPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class UserPostsFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public UserPostsFragment() {
        //empty constructor
    }

    /**
     * Stores a list of friends list
     */
    private List<String> friendsList;
    /**
     * Stores a reference of userPostsSwipeRefreshLayout
     */
    private SwipeRefreshLayout userPostsSwipeRefreshLayout;
    /**
     * Stores a reference of userPostsProgressBar
     */
    private ProgressBar userPostsProgressBar;
    /**
     * Stores an arraylist of userPosts
     */
    private ArrayList<UserPost> userPostArrayList;
    /**
     * Stores a reference of floatingActionButton
     */
    private FloatingActionButton floatingActionButton;
    /**
     * Stores a reference of userPostsRecyclerView
     */
    private RecyclerView userPostsRecyclerView;
    /**
     * Stores a reference of activityResultLauncher imageChooserActivityResultLauncher
     */
    private ActivityResultLauncher<?> imageChooserActivityResultLauncher;

    /**
     * Constructor initialises friendsList
     * @param friendsList
     */
    public UserPostsFragment(List<String> friendsList) {
        this.friendsList = friendsList;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_user_posts, container, false);
        //Initialising various views
        floatingActionButton = view.findViewById(R.id.floatingActionButton);
        userPostsRecyclerView = view.findViewById(R.id.userPostsRecyclerView);
        userPostsProgressBar = view.findViewById(R.id.userPostsProgressBar);
        userPostsSwipeRefreshLayout = view.findViewById(R.id.userPostsSwipeRefreshLayout);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        userPostsSwipeRefreshLayout.setOnRefreshListener(this);
        // Initialising activityResultContract which results in getting result from cropping activity
        ActivityResultContract<?, Uri> cropActivityResultContract = new ActivityResultContract<Object, Uri>() {
            @NonNull
            @NotNull
            @Override
            public Intent createIntent(@NonNull @NotNull Context context, Object input) {
                return CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .getIntent(getContext());
            }

            @Override
            public Uri parseResult(int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent intent) {
                if (intent != null) {
                    return CropImage.getActivityResult(intent).getUri();
                } else {
                    return null;
                }
            }
        };

        //Adding userPosts to the parse server
        imageChooserActivityResultLauncher = registerForActivityResult(cropActivityResultContract, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if(result != null){
                    userPostsProgressBar.setVisibility(View.VISIBLE);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), result);
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                        ParseFile currentUserPost = new ParseFile("userPost.png", byteArrayOutputStream.toByteArray());
                        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPosts");
                        query.whereEqualTo("username", ParseUser.getCurrentUser().getUsername());
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if(e == null){
                                    if(!objects.isEmpty()){
                                        for(ParseObject parseObject:objects){
                                            parseObject.add("userPosts", currentUserPost);
                                            try {
                                                parseObject.save();
                                                showAlert("Upload Successfull", "User post has been successfully uploaded");
                                            } catch (ParseException parseException) {
                                                parseException.printStackTrace();
                                            }
                                        }
                                    }else{
                                        ParseObject userPosts = new ParseObject("UserPosts");
                                        userPosts.put("username", ParseUser.getCurrentUser().getUsername());
                                        userPosts.add("userPosts", currentUserPost);
                                        try {
                                            userPosts.save();
                                            showAlert("Upload Successfull", "User post has been successfully uploaded");
                                        } catch (ParseException parseException) {
                                            parseException.printStackTrace();
                                        }
                                    }
                                }else{
                                    showAlert("Error", "Something went wrong: "+ e.getMessage());
                                }
                                refreshPosts();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooserActivityResultLauncher.launch(null);
            }
        });
        //Setting adapter of userPosts and setting the adapter to userPostsRecyclerView
        userPostArrayList = new ArrayList<>();
        UserPostAdapter userPostAdapter = new UserPostAdapter(userPostArrayList);
        userPostsRecyclerView.setAdapter(userPostAdapter);
        userPostsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    //Showing alert dialog box
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
    public void onResume() {
        super.onResume();
        refreshPosts();
    }

    /**
     * Adding posts to an arraylist of posts from Parse server and showing user posts to the recycler view
     */
    private void refreshPosts() {
        userPostsProgressBar.setVisibility(View.VISIBLE);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserPosts");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    userPostArrayList.clear();
                    if (!objects.isEmpty()) {
                        for (ParseObject parseObject : objects) {
                            String currentUsername = parseObject.getString("username");
                            if(friendsList.contains(currentUsername) || currentUsername.equals(ParseUser.getCurrentUser().getUsername())) {
                                List<ParseFile> userPosts = parseObject.getList("userPosts");
                                if(userPosts != null){
                                    for(ParseFile parseFile:userPosts){
                                        if(parseFile!= null){
                                            try {
                                                Bitmap bitmap = BitmapFactory.decodeByteArray(parseFile.getData(), 0, parseFile.getData().length);
                                                userPostArrayList.add(new UserPost(currentUsername, bitmap));
                                            } catch (ParseException parseException) {
                                                parseException.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                        UserPostAdapter userPostAdapter = new UserPostAdapter(userPostArrayList);
//                        userPostsRecyclerView.setAdapter(userPostAdapter);
                        userPostsRecyclerView.getAdapter().notifyDataSetChanged();
                    }
                } else {
                    showAlert("Error", "Something went wrong: " + e.getMessage());
                }
                userPostsProgressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onRefresh() {
        refreshPosts();
        userPostsSwipeRefreshLayout.setRefreshing(false);
    }
}