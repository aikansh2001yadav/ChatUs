package com.example.instagramcloneapp.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.instagramcloneapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.parse.FindCallback;
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
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileFragment extends Fragment {

    public UserProfileFragment() {
        //empty constructor
    }

    /**
     * Stores the name of current user
     */
    private String currentUser;
    /**
     * Stores name of the gender
     */
    private String gender;
    /**
     * Stores a reference of nameEditText which takes the name of current user as an input
     */
    private TextInputEditText nameEditText;
    /**
     * Stores a reference of lastNameEditText which takes the last name of current user as an input
     */
    private TextInputEditText lastNameEditText;
    /**
     * Stores a reference of ageEditText which takes the age of the current user as an input
     */
    private TextInputEditText ageEditText;
    /**
     * Stores a reference of genderSpinner which shows gender type of the current user
     */
    private Spinner genderSpinner;
    /**
     * Stores a reference of multiLineEditText which takes bio of the current user as an input
     */
    private TextInputEditText multiLineEditText;
    /**
     * Stores a reference of updateButton which is used to update profile details
     */
    private LinearLayout updateButton;
    /**
     * Stores a reference of detailsRefreshProgressBar
     */
    private ProgressBar detailsRefreshProgressBar;
    /**
     * Stores a reference of profileLinearLayout
     */
    private LinearLayout profileLinearLayout;
    /**
     * Stores a reference of profile image view
     */
    private CircleImageView profileImageView;
    private ActivityResultLauncher<?> cropResultLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        //Initialising various views
        nameEditText = view.findViewById(R.id.nameEditText);
        lastNameEditText = view.findViewById(R.id.lastNameEditText);
        ageEditText = view.findViewById(R.id.ageEditText);
        genderSpinner = view.findViewById(R.id.genderSpinner);
        multiLineEditText = view.findViewById(R.id.bioMultiLineEditText);
        updateButton = view.findViewById(R.id.btn_update);
        detailsRefreshProgressBar = view.findViewById(R.id.detailsRefreshProgressBar);
        profileLinearLayout = view.findViewById(R.id.progressLinearLayout);
        profileImageView = view.findViewById(R.id.profileImageView);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        profileLinearLayout.setVisibility(View.GONE);
        detailsRefreshProgressBar.setVisibility(View.VISIBLE);
        //Storing the name of current user
        currentUser = ParseUser.getCurrentUser().getUsername();

        ActivityResultContract<?, Uri> cropActivityResultContract = new ActivityResultContract<Object, Uri>() {
            @NonNull
            @NotNull
            @Override
            public Intent createIntent(@NonNull @NotNull Context context, Object input) {
                return CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.OVAL)
                        .setAspectRatio(1, 1)
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

        cropResultLauncher = registerForActivityResult(cropActivityResultContract, new ActivityResultCallback<Uri>() {
            @Override
            public void onActivityResult(Uri result) {
                if (result != null) {
                    profileImageView.setImageURI(result);
                }
            }
        });

        updateInformation(false);
        String[] genderList = {"Male", "Female", "TransGender"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, genderList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_item);

        genderSpinner.setAdapter(arrayAdapter);
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = genderList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Used to update all the details after clicking on updateButton
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable drawable = profileImageView.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);

                ParseFile parseFile = new ParseFile("currentProfilePicture.png", byteArrayOutputStream.toByteArray());
                detailsRefreshProgressBar.setVisibility(View.VISIBLE);
                if(!nameEditText.getText().toString().isEmpty() && !lastNameEditText.getText().toString().isEmpty() && !ageEditText.getText().toString().isEmpty() && gender != null){
                    ParseObject userProfile = new ParseObject("UserProfile");
                    userProfile.put("Username", currentUser);
                    userProfile.put("Name", nameEditText.getText().toString());
                    userProfile.put("LastName", lastNameEditText.getText().toString());
                    userProfile.put("Age", ageEditText.getText().toString());
                    userProfile.put("Gender", gender);
                    userProfile.put("Bio", multiLineEditText.getText().toString());
                    userProfile.put("ProfilePicture", parseFile);
                    userProfile.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null){
                                updateInformation(true);
                            }else{
                                detailsRefreshProgressBar.setVisibility(View.GONE);
                                showAlert("Update Error", "An error has occurred: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropResultLauncher.launch(null);
            }
        });
    }

    /**
     * Updates profile information from details stores in parse server
     * @param showAlert
     */
    private void updateInformation(boolean showAlert) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("UserProfile");
        query.whereEqualTo("Username", currentUser);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e == null){
                    if(!objects.isEmpty()){
                        try {
                            if(objects.size() > 1){
                                objects.get(0).delete();
                            }
                        } catch (ParseException parseException) {
                            parseException.printStackTrace();
                        }
                        for(ParseObject parseObject: objects){
                            nameEditText.setText(parseObject.getString("Name"));
                            lastNameEditText.setText(parseObject.getString("LastName"));
                            ageEditText.setText(parseObject.getString("Age"));
                            String currentGender = parseObject.getString("Gender");
                            if(currentGender != null){
                                if(currentGender.equals("Male")){
                                    genderSpinner.setSelection(0);
                                }else if(currentGender.equals("Female")){
                                    genderSpinner.setSelection(1);
                                }else{
                                    genderSpinner.setSelection(2);
                                }
                            }
                            String bio = parseObject.getString("Bio");
                            if(bio != null){
                                multiLineEditText.setText(bio);
                            }

                            ParseFile parseFile = parseObject.getParseFile("ProfilePicture");
                            if(parseFile != null && parseFile.isDataAvailable()){
                                try {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(parseFile.getData(), 0, parseFile.getData().length);
                                    profileImageView.setImageBitmap(bitmap);
                                } catch (ParseException parseException) {
                                    parseException.printStackTrace();
                                }
                            }
                        }
                        if(showAlert) {
                            showAlert("Details Update", "Your details has been successfully updated");
                        }
                    }
                    else{
                        showAlert("No info found", "Update information first and try again");
                    }
                }else{
                    showAlert("An error has occurred", "There has been an error: " + e.getMessage());
                }
                detailsRefreshProgressBar.setVisibility(View.GONE);
                profileLinearLayout.setVisibility(View.VISIBLE);
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
}