package com.example.projectprototype;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projectprototype.databinding.ActivitySecondBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

public class SecondActivity extends AppCompatActivity {

    // Define maximum number of uploads allowed
    private static final int MAX_DISPLAY = 5;

    ActivitySecondBinding binding;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    MyAdapter adapter;
    ArrayList<String> mediaIdList;
    SharedPreferences sharedPreferences;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    // Reference to the specific user's media node in Firebase Database
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("media").child(uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_second);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize data structures
        dataList = new ArrayList<>();
        mediaIdList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        adapter = new MyAdapter(dataList, mediaIdList, this, sharedPreferences);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchDataFromDatabase();

        // Set up BottomNavigationView
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_profile) {
                startActivity(new Intent(SecondActivity.this, ProfileActivity.class));
            } else if (id == R.id.nav_upload) {
                startActivity(new Intent(SecondActivity.this, UploadActivity.class));
            } else if (id == R.id.nav_archive) {
                startActivity(new Intent(SecondActivity.this, ArchiveActivity.class));
            }
            return true;
        });
    }

    private void fetchDataFromDatabase() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear();
                mediaIdList.clear();

                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
                String todayDate = sdf.format(new Date());

                int postCount = 0;
                ArrayList<DataClass> allPosts = new ArrayList<>();
                ArrayList<String> allMediaIds = new ArrayList<>();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    String mediaId = dataSnapshot.getKey();

                    if (dataClass != null) {
                        allPosts.add(dataClass);
                        allMediaIds.add(mediaId);
                    }
                }

                // Add today's posts
                for (int i = 0; i < allPosts.size() && postCount < MAX_DISPLAY; i++) {
                    DataClass dataClass = allPosts.get(i);
                    String mediaId = allMediaIds.get(i);

                    if (todayDate.equals(dataClass.getTimestamp())) {
                        dataList.add(dataClass);
                        mediaIdList.add(mediaId);
                        postCount++;
                    }
                }

                // Add random posts from the archive if necessary
                if (postCount < MAX_DISPLAY) {
                    Collections.shuffle(allPosts);
                    for (int i = 0; i < allPosts.size() && postCount < MAX_DISPLAY; i++) {
                        DataClass randomPost = allPosts.get(i);
                        String mediaId = allMediaIds.get(i);

                        if (!dataList.contains(randomPost)) {
                            dataList.add(randomPost);
                            mediaIdList.add(mediaId);
                            postCount++;
                        }
                    }
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Failed to fetch data", error.toException());
            }
        });
    }
}

