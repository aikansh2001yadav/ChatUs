package com.example.instagramcloneapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.instagramcloneapp.R;
import com.parse.ParseUser;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //If the user has already logged up then skip loginActivity
        if(ParseUser.getCurrentUser() != null){
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            intent.putExtra("username", ParseUser.getCurrentUser().getUsername());
            startActivity(intent);
            finish();
        }else {
            //Delays 2 seconds for starting LoginActivity
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 2000);
        }
    }
}