package com.example.instagramcloneapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class SignupActivity extends AppCompatActivity {

    /**
     * Storing a reference of emailSignupEditText
     */
    private EditText emailSignupEditText;
    /**
     * Storing a reference of usernameSignupEditText
     */
    private EditText usernameSignupEditText;
    /**
     * Storing a reference of passwordSignupEditText
     */
    private EditText passwordSignupEditText;
    /**
     * Storing a reference of signupButton which is used to signup new user
     */
    private Button signupButton;
    /**
     * Storing a reference of signupProgressBar
     */
    private ProgressBar signupProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Initialising views
        emailSignupEditText = findViewById(R.id.emailSignupEditText);
        usernameSignupEditText = findViewById(R.id.usernameSignupEditText);
        passwordSignupEditText = findViewById(R.id.passwordSignupEditText);
        signupButton = findViewById(R.id.signupButton);
        signupProgressBar = findViewById(R.id.signupProgressBar);
        signupProgressBar.setVisibility(View.GONE);

        //Setting on click listener on signupButton
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                signupProgressBar.setVisibility(View.VISIBLE);
                ParseUser newUser = new ParseUser();
                newUser.setEmail(emailSignupEditText.getText().toString());
                newUser.setUsername(usernameSignupEditText.getText().toString());
                newUser.setPassword(passwordSignupEditText.getText().toString());

                //Signing up new user
                newUser.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            ParseUser.logOut();
                            showAlert("Account Created Successfully", "Please verify your email before Login");
                        }else{
                            ParseUser.logOut();
                            showAlert("Account creation failed", "Account can't be created: " + e.getMessage());
                        }
                    }
                });
            }
        });
    }

    /**
     * Showing alert
     * @param title
     * @param message
     */
    private void showAlert(String title, String message) {
        signupProgressBar.setVisibility(View.GONE);
        new AlertDialog.Builder(SignupActivity.this)
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

    /**
     * Closing keyboard
     */
    private void closeKeyboard()
    {
        // this will give us the view
        // which is currently focus
        // in this layout
        View view = this.getCurrentFocus();

        // if nothing is currently
        // focus then this will protect
        // the app from crash
        if (view != null) {

            // now assign the system
            // service to InputMethodManager
            InputMethodManager manager
                    = (InputMethodManager)
                    getSystemService(
                            Context.INPUT_METHOD_SERVICE);
            manager
                    .hideSoftInputFromWindow(
                            view.getWindowToken(), 0);
        }
    }
}