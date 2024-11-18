package com.example.projectprototype;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.projectprototype.databinding.ActivityTimelineBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TimelineActivity extends AppCompatActivity {

    ActivityTimelineBinding binding;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("media").child(uid);
    ArrayList<DataClass> dataArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataArray = new ArrayList<>();

        // RecyclerView and Adapter
        RecyclerView recyclerView = findViewById(R.id.timelineRecyclerView);
        TimelineAdapter adapter = new TimelineAdapter(this, dataArray);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataArray.add(dataClass);
                }
                adapter.notifyDataSetChanged();

                // Sort dataArray in descending order based on date
                Collections.sort(dataArray, new Comparator<DataClass>() {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                    @Override
                    public int compare(DataClass o1, DataClass o2) {
                        try {
                            Date date1 = dateFormat.parse(o1.getDate());
                            Date date2 = dateFormat.parse(o2.getDate());
                            return date2.compareTo(date1); // Descending order
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return 0;
                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((item)->{
            if(item.getItemId() == R.id.nav_home){
                Intent intent = new Intent(TimelineActivity.this, SecondActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(TimelineActivity.this,ProfileActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_upload) {
                Intent intent = new Intent(TimelineActivity.this, UploadActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_archive) {
                Intent intent = new Intent(TimelineActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
            return true;
        });

    }
}