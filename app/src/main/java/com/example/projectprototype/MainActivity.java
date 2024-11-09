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


    private MaterialButton continueBtn;
    private EditText patientOrFamily;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        patientOrFamily = findViewById(R.id.patientOrfamily);
        continueBtn = findViewById(R.id.continuebtn);

        //Set up continue button click event
        continueBtn.setOnClickListener(v -> {
            //Check user input to determine if it is patient login or family login
            String pOrf = patientOrFamily.getText().toString().toLowerCase().trim();
            if(pOrf.equals("patient")){ //if the user type in "patient", then switch to
                // login screen where user use email and password to login or sign up
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                Toast.makeText(MainActivity.this, "Switch to Login Page", Toast.LENGTH_SHORT).show();
            } else if (pOrf.equals("family")) {
                // if the user type in "family","family members","friend", & "friends",then switch to UID login screen
                startActivity(new Intent(MainActivity.this, UIDActivity.class));
                Toast.makeText(MainActivity.this, "Switch to UID Login Page", Toast.LENGTH_SHORT).show();
            }
            else{
                //if the user type nothing, then show the message "please fill in the field"
                Toast.makeText(MainActivity.this, "Please fill in the field", Toast.LENGTH_SHORT).show();
            }

        });




    }


}