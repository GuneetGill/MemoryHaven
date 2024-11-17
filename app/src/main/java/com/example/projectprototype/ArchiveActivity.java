package com.example.projectprototype;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

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

    ArrayList<DataClass> dataList2;

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
        EdgeToEdge.enable(this);
        binding = ActivityArchiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        gridView2 = findViewById(R.id.gridView);
        dataList2 = new ArrayList<>();
        adapter2 = new MyAdapter2(dataList2,this);
        gridView2.setAdapter(adapter2); //set adapter here
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList2.add(dataClass);
                }
                adapter2.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        timelineButton = findViewById(R.id.timelineButton);


        timelineButton.setOnClickListener(v -> {
            // Navigate to Timeline Archive
            Intent intent = new Intent(this, TimelineActivity.class);
            startActivity(intent);
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((item)->{
            if(item.getItemId() == R.id.nav_home){
                Intent intent = new Intent(ArchiveActivity.this, SecondActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(ArchiveActivity.this,ProfileActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_upload) {
                Intent intent = new Intent(ArchiveActivity.this, UploadActivity.class);
                startActivity(intent);
            }
            else if (item.getItemId() == R.id.nav_archive) {
                Intent intent = new Intent(ArchiveActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
            return true;
        });

        // Initialize and set up the Key Word Search Button and Input Bar
        keyWordButton = findViewById(R.id.keyWordButton);
        keywordInput = findViewById(R.id.keywordInput);
        keywordInput.setVisibility(View.GONE); // Hide input bar initially

        // Set up the button click listener
        keyWordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (keywordInput.getVisibility() == View.GONE) {
                    // Show the input bar
                    keywordInput.setVisibility(View.VISIBLE);
                } else {
                    // Hide the input bar
                    keywordInput.setVisibility(View.GONE);
                }
            }
        });

        //set the listener for when user enters input into text field
        keywordInput.setOnEditorActionListener((v, actionId, event) -> {
            String keyword = keywordInput.getText().toString().trim();

            // Call the search function
            searchKeyword(keyword);

            // Clear the input field
            keywordInput.setText("");
            return true; // Indicates the action was handled
        });


    }

    //search keyword method
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