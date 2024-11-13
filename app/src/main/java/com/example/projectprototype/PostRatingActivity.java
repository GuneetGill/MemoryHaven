package com.example.projectprototype;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PostRatingActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private Button submitRatingButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating_post);

        // Retrieve the unique Firebase key (mediaId) from the Intent
        String mediaId = getIntent().getStringExtra("MEDIA_ID");

        // Retrieve the current user's UID
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize Firebase database reference to point to the specific media item
        databaseReference = FirebaseDatabase.getInstance()
                .getReference("media")
                .child(uid)           // User ID path
                .child(mediaId);      // Media ID path

        // Find views by ID
        ratingBar = findViewById(R.id.ratingBar);
        submitRatingButton = findViewById(R.id.submitRatingButton);

        // Set up the submit button's onClick listener
        submitRatingButton.setOnClickListener(v -> {
            // Get the rating from the RatingBar
            float rating = ratingBar.getRating();

            // Get the current time
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

            // Create a map to store rating and timeRated directly in the existing media item
            Map<String, Object> updates = new HashMap<>();
            updates.put("rating", rating);
            updates.put("timeRated", currentTime);

            // Update the existing media item with the rating and timeRated fields
            databaseReference.updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(PostRatingActivity.this, "Rating submitted!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(PostRatingActivity.this, "Failed to submit rating.", Toast.LENGTH_SHORT).show());
        });
    }
}
