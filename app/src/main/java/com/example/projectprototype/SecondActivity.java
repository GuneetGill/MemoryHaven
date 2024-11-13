package com.example.projectprototype;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {

    ActivitySecondBinding binding;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    ArrayList<String> mediaIdList; // New list for Firebase keys
    MyAdapter adapter;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    String uid = auth.getCurrentUser().getUid();
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("media").child(uid);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySecondBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_second);

        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        mediaIdList = new ArrayList<>(); // Initialize the list for media IDs
        adapter = new MyAdapter(dataList, mediaIdList, this);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataList.clear(); // Clear the lists to avoid duplicates
                mediaIdList.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    String mediaId = dataSnapshot.getKey(); // Get the Firebase key

                    dataList.add(dataClass); // Add the DataClass object
                    mediaIdList.add(mediaId); // Add the key to the mediaIdList
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle errors here
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecondActivity.this, UploadActivity.class);
                startActivity(intent);
                finish();
            }
        });

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.nav_profile) {
                Intent intent = new Intent(SecondActivity.this, ProfileActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_upload) {
                Intent intent = new Intent(SecondActivity.this, UploadActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_archive) {
                Intent intent = new Intent(SecondActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }
            return true;
        });
    }
}
