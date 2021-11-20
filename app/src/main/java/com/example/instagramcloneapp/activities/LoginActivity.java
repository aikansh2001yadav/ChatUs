package com.example.instagramcloneapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramcloneapp.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class LoginActivity extends AppCompatActivity {

    /**
     * Stores a reference of usernameLoginEditText
     */
    private EditText usernameLoginEditText;
    /**
     * Stores a reference of passwordLoginEditText
     */
    private EditText passwordLoginEditText;
    /**
     * Stores a reference of loginButton which is used to login user
     */
    private LinearLayout loginButton;
    /**
     * Stores a reference of newUserButton which is used to startActivity for user registration
     */
    private TextView registerButton;
    /**
     * Stores a reference of progressBar
     */
    private ProgressBar loginProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginProgressBar = findViewById(R.id.activity_login_progress);

        usernameLoginEditText = findViewById(R.id.text_login_username);
        passwordLoginEditText = findViewById(R.id.text_login_password);
        loginButton = findViewById(R.id.btn_login);
        registerButton = findViewById(R.id.text_register);

        //Setting on click listener on loginButton to login user
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                loginProgressBar.setVisibility(View.VISIBLE);
                ParseUser.logInInBackground(usernameLoginEditText.getText().toString(), passwordLoginEditText.getText().toString(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(e == null){
                            if(user != null){
                                showAlert("Login Successful", "The user is successfully logged in", true);
                            }else{
                                ParseUser.logOut();
                                showAlert("Login failed", "User can't be logged in: " + " Please try again", false);
                            }
                        }else{
                            showAlert("Something went wrong", e.getMessage() + " Please try again", false);
                        }
                    }
                });
            }
        });

        //Setting on click listener on newUserButton
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgressBar.setVisibility(View.GONE);
                //Starting intent from loginActivity to SignupActivity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Showing alert
     * @param title
     * @param message
     * @param startActivity
     */
    private void showAlert(String title, String message, boolean startActivity) {
        loginProgressBar.setVisibility(View.GONE);
        new AlertDialog.Builder(LoginActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if(startActivity) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
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

    @Override
    protected void onRestart() {
        super.onRestart();
    }
}