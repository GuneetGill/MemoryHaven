package com.example.projectprototype;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.projectprototype.databinding.ActivityArchiveBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ArchiveActivity extends AppCompatActivity {
    ActivityArchiveBinding binding;
    GridView gridView2;

    ArrayList<DataClass> dataList2; // List for post data
    ArrayList<String> mediaIdList; // List for Firebase keys

    MyAdapter2 adapter2;

    Button keyWordButton;
    Button timelineButton;
    EditText keywordInput;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("media").child(uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gridView2 = findViewById(R.id.gridView);
        dataList2 = new ArrayList<>();
        mediaIdList = new ArrayList<>(); // Initialize the mediaIdList
        adapter2 = new MyAdapter2(dataList2, this);
        gridView2.setAdapter(adapter2); // Set adapter here

        // Fetch data from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList2.clear(); // Clear previous data
                mediaIdList.clear(); // Clear previous keys
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    if (dataClass != null && dataSnapshot.getKey() != null) { // Ensure key is not null
                        dataList2.add(dataClass);
                        mediaIdList.add(dataSnapshot.getKey()); // Save the Firebase key
                    }
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ArchiveActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });

        // Handle clicks on GridView items
        gridView2.setOnItemClickListener((parent, view, position, id) -> {
            String mediaId = mediaIdList.get(position); // Get the Firebase key for this item

            if (mediaId == null || mediaId.isEmpty()) {
                Toast.makeText(this, "Media ID is missing for this post", Toast.LENGTH_SHORT).show();
                return;
            }

            // Pass the media ID to RatingsAnalysisActivity
            Intent intent = new Intent(ArchiveActivity.this, RatingsAnalysisActivity.class);
            intent.putExtra("postId", mediaId); // Pass the Firebase key
            startActivity(intent);
        });

        // Set up timeline button
        timelineButton = findViewById(R.id.timelineButton);
        timelineButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
        });

        // Bottom navigation setup
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(ArchiveActivity.this, SecondActivity.class));
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(ArchiveActivity.this, ProfileActivity.class));
            } else if (item.getItemId() == R.id.nav_upload) {
                startActivity(new Intent(ArchiveActivity.this, UploadActivity.class));
            } else if (item.getItemId() == R.id.nav_archive) {
                startActivity(new Intent(ArchiveActivity.this, ArchiveActivity.class));
            }
            return true;
        });

        // Initialize and set up the keyword search button and input bar
        keyWordButton = findViewById(R.id.keyWordButton);
        keywordInput = findViewById(R.id.keywordInput);
        keywordInput.setVisibility(View.GONE); // Hide input bar initially

        // Set up the button click listener
        keyWordButton.setOnClickListener(v -> {
            if (keywordInput.getVisibility() == View.GONE) {
                // Show the input bar
                keywordInput.setVisibility(View.VISIBLE);
            } else {
                // Hide the input bar
                keywordInput.setVisibility(View.GONE);
            }
        });

        // Set the listener for when user enters input into the text field
        keywordInput.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = keywordInput.getText().toString().trim();

            // Call the search function
            searchKeyword(keyword);

            // Clear the input field
            keywordInput.setText("");
            return true; // Indicates the action was handled
        });
    }

    // Search keyword method
    private void searchKeyword(String keyword) {
        // Ensure the keyword is not empty
        if (keyword.isEmpty()) {
            Toast.makeText(this, "Please enter a keyword", Toast.LENGTH_SHORT).show();
            return;
        }

        // Temporary list to store matching posts
        ArrayList<DataClass> filteredList = new ArrayList<>();

        // Query the Firebase database for the user's posts
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);

                    // Check if the post has a caption and if it contains the keyword
                    if (dataClass != null && dataClass.getCaption() != null &&
                            dataClass.getCaption().toLowerCase().contains(keyword.toLowerCase())) {
                        filteredList.add(dataClass);
                    }
                }

                // Check if any matches were found
                if (filteredList.isEmpty()) {
                    Toast.makeText(ArchiveActivity.this, "No results found for: " + keyword, Toast.LENGTH_SHORT).show();
                } else {
                    // Update the GridView with the filtered list
                    adapter2 = new MyAdapter2(filteredList, ArchiveActivity.this);
                    gridView2.setAdapter(adapter2);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ArchiveActivity.this, "Failed to search: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
