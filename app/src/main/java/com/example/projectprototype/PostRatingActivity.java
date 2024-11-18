package com.example.projectprototype;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
            float newRating = ratingBar.getRating();

            // Get the current time
            String currentTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(new Date());

            // Fetch existing arrays for ratings and timeRated
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    ArrayList<Float> ratingsList = new ArrayList<>();
                    ArrayList<String> timeRatedList = new ArrayList<>();

                    // Retrieve existing ratings array if present
                    if (snapshot.child("rating").exists()) {
                        ratingsList = (ArrayList<Float>) snapshot.child("rating").getValue();
                    }

                    // Retrieve existing timeRated array if present
                    if (snapshot.child("timeRated").exists()) {
                        timeRatedList = (ArrayList<String>) snapshot.child("timeRated").getValue();
                    }

                    // Add new entries to the arrays
                    ratingsList.add(newRating);
                    timeRatedList.add(currentTime);

                    // Update Firebase with the modified arrays
                    databaseReference.child("rating").setValue(ratingsList);
                    databaseReference.child("timeRated").setValue(timeRatedList)
                            .addOnSuccessListener(aVoid -> Toast.makeText(PostRatingActivity.this, "Rating submitted!", Toast.LENGTH_SHORT).show())
                            .addOnFailureListener(e -> Toast.makeText(PostRatingActivity.this, "Failed to submit rating.", Toast.LENGTH_SHORT).show());
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    Toast.makeText(PostRatingActivity.this, "Error fetching data.", Toast.LENGTH_SHORT).show();
                }
            });
            startActivity(new Intent(PostRatingActivity.this, SecondActivity.class));
            }

        );
    }
}
