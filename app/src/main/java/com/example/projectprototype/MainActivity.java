package com.example.projectprototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private Button patientButton, familyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Initialize views
        patientButton = findViewById(R.id.patientBtn);
        familyButton = findViewById(R.id.familyBtn);
        TextView btnSignUp = findViewById(R.id.textViewSignUp);
        //Set up "Sign Up" button to navigate to RegisterActivity
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );
        //Set up the on click event for patient and family
        //If the user tap "patient", then switch to email and password login screen
        patientButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            Toast.makeText(MainActivity.this, "Switch to Email and Password login screen", Toast.LENGTH_SHORT).show();
            finish();
        });
        //If the user tap "family", then switch to UID login screen
        familyButton.setOnClickListener(v ->{
            startActivity(new Intent(MainActivity.this, UIDActivity.class));
            Toast.makeText(MainActivity.this, "Switch to UID login screen", Toast.LENGTH_SHORT).show();
            finish();
        });






    }


}