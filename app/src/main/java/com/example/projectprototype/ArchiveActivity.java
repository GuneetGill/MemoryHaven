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
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;

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
            return true;
        });


    }


}