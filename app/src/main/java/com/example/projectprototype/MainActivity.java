package com.example.projectprototype;

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

    private FirebaseAuth mAuth;
    private EditText emailaddress, password,joinCode;
    private MaterialButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize views
        emailaddress = findViewById(R.id.emailaddress);
        password = findViewById(R.id.password);
        login = findViewById(R.id.loginbtn);
        joinCode = findViewById(R.id.joinCode);
        TextView btnSignUp = findViewById(R.id.textViewSignUp);

        // Set up "Sign Up" button to navigate to RegisterActivity
        btnSignUp.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );

        //Set up login Button click event
        login.setOnClickListener(v ->{
            String email = emailaddress.getText().toString().trim();
            String pass = password.getText().toString().trim();
            String code = joinCode.getText().toString().trim();

            //Check if the join code is provided for UID login
            if(!code.isEmpty()){
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null){
                    String currentUid = currentUser.getUid();;
                    if(currentUid.equals(code)){
                        //UID match, user is authenticated
                        Toast.makeText(MainActivity.this, "UID Signed In", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,SecondActivity.class));

                    }else{
                        //UID does not match, user is not authenticated
                        Toast.makeText(MainActivity.this,"UID does not match",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    // No user signed in
                    Toast.makeText(MainActivity.this, "No user signed in", Toast.LENGTH_SHORT).show();
                }
            } else if (email.isEmpty() || pass.isEmpty()) { //Check all fields are filled in
                Toast.makeText(MainActivity.this, "Please fill in all field", Toast.LENGTH_SHORT).show();

            }else{
                loginUser(email,pass);
            }
        });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();

                        // Proceed to the next activity
                        startActivity(new Intent(MainActivity.this, SecondActivity.class));
                        finish();  // Finish this activity so it can't be returned to with back button
                    } else {
                        // Login failed
                        Toast.makeText(MainActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}