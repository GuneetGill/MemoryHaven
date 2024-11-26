package com.example.projectprototype;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.material.button.MaterialButton;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailaddress, password;
    private MaterialButton login;
    private Button returnBack;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // Initialize views
        emailaddress = findViewById(R.id.emailaddress); //variable for email address
        password = findViewById(R.id.password); //variable for password
        login = findViewById(R.id.loginbtn); //button for login
        returnBack = findViewById(R.id.returnTomainBtn); //button return back to home screen

        //Set up login for toggle password view
        password.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIHGT = 2; //index for right drawable
                if(event.getAction() == MotionEvent.ACTION_UP){
                    if(event.getRawX() >= (password.getRight() - password.getCompoundDrawables()[DRAWABLE_RIHGT].getBounds().width())){
                        togglePasswordVisibility();
                        return true;
                    }
                }
                return false;
            }
        });

        //Set up login button click event
        login.setOnClickListener(v -> {
            // get email
            String email = emailaddress.getText().toString().trim();
            // get password
            String pass = password.getText().toString().trim();
            //Check if the email or password match the one in database
            //If the email or password is empty, then show the message "please fill in all fields"
            if(email.isEmpty() || pass.isEmpty()){
                Toast.makeText(LoginActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            }
            //Check the credentials
            else{
                loginUser(email,pass);
            }
        });

        //Set up return back to home button
        returnBack.setOnClickListener(v ->{
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
            Toast.makeText(LoginActivity.this, "Switch back to Main Page", Toast.LENGTH_SHORT).show();
        });
    }
    //Login user function
    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Login successful
                        FirebaseUser user = mAuth.getCurrentUser();
                        // Proceed to the next activity
                        Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, SecondActivity.class));
                        finish();  // Finish this activity so it can't be returned to with back button
                        //Indicator for flagging the login method
                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("isUidLogin", false);
                        editor.apply();
                    } else {
                        // Login failed
                        Toast.makeText(LoginActivity.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //Toggle password view function
    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Hide password
            password.setTransformationMethod(PasswordTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
        } else {
            // Show password
            password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            password.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.password_icon, 0);
        }
        isPasswordVisible = !isPasswordVisible;

        // Move cursor to the end of the text
        password.setSelection(password.length());
    }
}