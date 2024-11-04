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

// The Main/Home Page -> the feed
    // Need to create a another button in the menu -> archive
    //


    ActivitySecondBinding binding;
    FloatingActionButton fab;
    RecyclerView recyclerView;
    ArrayList<DataClass> dataList;
    MyAdapter adapter;
    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
      binding = ActivitySecondBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setContentView(R.layout.activity_second);


        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        dataList = new ArrayList<>();
        adapter = new MyAdapter(dataList,this);
        recyclerView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataList.add(dataClass);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        bottomNavigationView.setOnNavigationItemSelectedListener((item)->{
            if(item.getItemId() == R.id.nav_profile){ //if user click profile icon, switch to profile page
                Intent intent = new Intent(SecondActivity.this,ProfileActivity.class);
                startActivity(intent);
            } else if (item.getItemId() == R.id.nav_archive) { //if user click upload icon, switch to upload page
                Intent intent = new Intent(SecondActivity.this, ArchiveActivity.class);
                startActivity(intent);
            }

            return true;

        });

        
        

    }


}
