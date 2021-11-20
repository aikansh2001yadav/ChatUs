package com.example.instagramcloneapp.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramcloneapp.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends AppCompatActivity {

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
     * Storing a reference of confirmPasswordEditText
     */
    private EditText confirmPasswordEditText;
    /**
     * Storing a reference of signupButton which is used to signup new user
     */
    private LinearLayout registerButton;
    private CheckBox checkboxTerms;
    /**
     * Storing a reference of signupProgressBar
     */
    private ProgressBar signupProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialising views
        emailSignupEditText = findViewById(R.id.text_register_email);
        usernameSignupEditText = findViewById(R.id.text_register_username);
        passwordSignupEditText = findViewById(R.id.text_register_pass);
        confirmPasswordEditText = findViewById(R.id.text_confirm_password);
        registerButton = findViewById(R.id.btn_register);
        signupProgressBar = findViewById(R.id.progress_register_activity);
        checkboxTerms = findViewById(R.id.checkbox_terms);
        signupProgressBar.setVisibility(View.GONE);

        //Setting on click listener on signupButton
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeKeyboard();
                signupProgressBar.setVisibility(View.VISIBLE);
                if (validateDetails(emailSignupEditText.getText().toString(), usernameSignupEditText.getText().toString(), passwordSignupEditText.getText().toString(), confirmPasswordEditText.getText().toString())) {
                    ParseUser newUser = new ParseUser();
                    newUser.setEmail(emailSignupEditText.getText().toString());
                    newUser.setUsername(usernameSignupEditText.getText().toString());
                    newUser.setPassword(passwordSignupEditText.getText().toString());
                    //Signing up new user
                    newUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                ParseUser.logOut();
                                showAlert("Account Created Successfully", "Please verify your email before Login");
                            } else {
                                ParseUser.logOut();
                                showAlert("Account creation failed", "Account can't be created: " + e.getMessage());
                            }
                        }
                    });
                }else{
                    signupProgressBar.setVisibility(View.GONE);
                }
            }
        });

        findViewById(R.id.text_login_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
        new AlertDialog.Builder(RegisterActivity.this)
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
     * Validates input information given by the user and give suggestions
     */
    private boolean validateDetails(String email, String username, String password, String confirmPassword) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (username.isEmpty()) {
            Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (password.isEmpty() || password.length() < 6) {
            Toast.makeText(this, "Please enter password correctly", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (!confirmPassword.equals(password)) {
            Toast.makeText(this, "Entered password is not same as you typed earlier", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else if (!checkboxTerms.isChecked()) {
            Toast.makeText(this, "Please agree to all terms and conditions", Toast.LENGTH_SHORT)
                    .show();
            return false;
        } else {
            return true;
        }
    }

    /**
     * Closing keyboard
     */
    private void closeKeyboard() {
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