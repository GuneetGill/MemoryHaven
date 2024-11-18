package com.example.projectprototype;

import android.content.Intent;
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

    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    // Reference to the specific user's media node in Firebase Database
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("media").child(uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_second);

        //fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        // Initialize data structures
        dataList = new ArrayList<>();
        mediaIdList = new ArrayList<>();

        // Set up RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyAdapter(dataList, mediaIdList, this);
        recyclerView.setAdapter(adapter);

        // Fetch data from Firebase
        fetchDataFromDatabase();

        // Floating Action Button to navigate to UploadActivity
//        fab.setOnClickListener(v -> {
//            Intent intent = new Intent(SecondActivity.this, UploadActivity.class);
//            startActivity(intent);
//            finish();
//        });

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
//package com.example.projectprototype;
//
//import android.content.Intent;
//import android.os.Bundle;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.projectprototype.databinding.ActivitySecondBinding;
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.google.firebase.auth.FirebaseAuth;
//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//import com.google.firebase.database.ValueEventListener;
//
//import android.util.Log;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.Button;
//
//import java.text.SimpleDateFormat;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
//
////SecondActivity serves as the home screen of the app, displaying a list of media items from Firebase in a RecyclerView
//// displays multimedia feed with text and audio
//public class SecondActivity extends AppCompatActivity {
//
//    // Define maximum number of uploads allowed
//    private static final int MAX_DISPLAY = 5;
//
//    ActivitySecondBinding binding;
//    FloatingActionButton fab;
//    RecyclerView recyclerView;
//    ArrayList<DataClass> dataList;
//    MyAdapter adapter;
//    ArrayList<String> mediaIdList;
//
//    FirebaseAuth auth = FirebaseAuth.getInstance();
//    String uid = auth.getCurrentUser().getUid();
//    //gets data for specfic user
//    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("media").child(uid);
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ActivitySecondBinding.inflate(getLayoutInflater());
//        setContentView(R.layout.activity_second);
//
//
//        fab = findViewById(R.id.fab);
//        recyclerView = findViewById(R.id.recyclerView);
//        dataList = new ArrayList<>();
//        mediaIdList = new ArrayList<>();
//
//        //bind data from datalist
//        recyclerView.setHasFixedSize(true);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        adapter = new MyAdapter(dataList, mediaIdList, this);
//        recyclerView.setAdapter(adapter);
//
//
//
//        //fetching data from database
//        databaseReference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot)
//            {
//                // Clear the existing data to ensure you only show the latest posts
//                dataList.clear();
//
//                mediaIdList.clear();
//
//                // Get today's date in MM/dd/yyyy format
//                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
//                String todayDate = sdf.format(new Date()); // Current date in the MM/dd/yyyy format
//
//                // Limit the number of posts to first few based on MAX_DISPLAYS number
//                int postCount = 0;
//
//                int totalPostsArchive = 0;
//
//                ArrayList<DataClass> allPosts = new ArrayList<>();
//                ArrayList<String> allMediaIds = new ArrayList<>();
//
//
//                // Loop through each child node under `media/<user_id>`
//                for (DataSnapshot dataSnapshot : snapshot.getChildren())
//                {
//                    //count # of posts in arhive
//                    totalPostsArchive++;
//                    // Convert each child into a `DataClass` object
//                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
//
//
//                    // Add the `DataClass` object to the list if it’s not null
//                    if (dataClass != null) {
//                        allPosts.add(dataClass);
//                        String mediaId = dataSnapshot.getKey();
//                        mediaIdList.add(mediaId); // Add the mediaId to the list
//                        Log.d("MediaLog", "Media ID added: " + mediaId);
//                    }
//
//                }
//
//                //post new posts from today
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
//                    Log.d("mytag", "Raw DataSnapshot: " + dataSnapshot.getValue());
//                    //get data stored in database
//                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
//                    String databaseTimestamp = dataClass.getTimestamp(); //get timestamp
//
//                    //check date with todays date
//                    if (dataClass != null && todayDate.equals(databaseTimestamp)) {
//                        //if archive is less than max display we dont want duplicates
//                        if (postCount < MAX_DISPLAY && totalPostsArchive!= postCount) {
//                            dataList.add(dataClass); // Add the post to the list
//                            postCount++; // Increment the counter
//                            Log.d("mytag", "we are posting something from today's date");
//                        }
//
//                        // Stop adding posts once we have reached max
//                        if (postCount >= MAX_DISPLAY && totalPostsArchive!= postCount) {
//                            break;
//                        }
//                    }
//                }
//
//                //if no new posts from today then pick from archive
//                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                    //if post counter is less than display randomly from archive
//                    if (postCount < MAX_DISPLAY && totalPostsArchive!= postCount)
//                    {
//                        // Shuffle allPosts to get random order
//                        Collections.shuffle(allPosts);
//                        // Add posts from allPosts until reaching MAX_DISPLAY
//                        for (DataClass randomPost : allPosts) {
//                            if (postCount >= MAX_DISPLAY) {
//                                break;
//                            }
//                            // Ensure no duplicates
//                            if (!dataList.contains(randomPost)) {
//                                dataList.add(randomPost);
//                                postCount++;
//                                Log.d("mytag", "we are posting something from the archive");
//
//                            }
//                        }
//                    }
//
//                }
//
//                //ArrayList<DataClass> allPosts = new ArrayList<>();
////                String mediaId = dataSnapshot.getKey();
////                mediaIdList.add(mediaId);
//
//                // Notify the adapter that the data has changed
//                adapter.notifyDataSetChanged();
//            }
//
//
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });
//
//
//
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(SecondActivity.this, UploadActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });
//
//
//
//        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
//        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
//            if (item.getItemId() == R.id.nav_profile) {
//                Intent intent = new Intent(SecondActivity.this, ProfileActivity.class);
//                startActivity(intent);
//            } else if (item.getItemId() == R.id.nav_upload) {
//                Intent intent = new Intent(SecondActivity.this, UploadActivity.class);
//                startActivity(intent);
//            } else if (item.getItemId() == R.id.nav_archive) {
//                Intent intent = new Intent(SecondActivity.this, ArchiveActivity.class);
//                startActivity(intent);
//            }
//
//            return true;
//        });
//
//    }
//
//
//}
