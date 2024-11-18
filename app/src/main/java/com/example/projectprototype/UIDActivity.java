package com.example.projectprototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.android.material.button.MaterialButton;

public class UIDActivity extends AppCompatActivity {
    //This is the UID login page code
    private FirebaseAuth mAuth;

    private EditText joincode;
    private MaterialButton joinLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uid);

        //Initialize firebase authentication
        mAuth = FirebaseAuth.getInstance();

        //Initialize views
        joincode = findViewById(R.id.joincode);
        joinLogin = findViewById(R.id.joinCodeLogin);

        // Set up join code login button click event
        joinLogin.setOnClickListener(v -> {
            // Get the join code entered by the user
            String code = joincode.getText().toString().trim();
            //Check if the join code is provided for UID login
            if(!code.isEmpty()) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null) {
                    String currentUid = currentUser.getUid();
                    if (currentUid.equals(code)) {
                        //UID match, user is authenticated
                        startActivity(new Intent(UIDActivity.this, SecondActivity.class));
                        Toast.makeText(UIDActivity.this, "UID Signed In", Toast.LENGTH_SHORT).show();
                        finish();
                        //Indicator for flagging the login method
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isUidLogin", true);
                        editor.apply();
                    } else {
                        //UID does not match, user is not authenticated
                        Toast.makeText(UIDActivity.this, "UID does not match", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}