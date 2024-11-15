package com.example.projectprototype;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.bumptech.glide.Glide;
import com.example.projectprototype.databinding.ActivityTimelineBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class TimelineActivity extends AppCompatActivity {

    ActivityTimelineBinding binding;
    //FirebaseAuth auth = FirebaseAuth.getInstance();
    //String uid = auth.getCurrentUser().getUid();

    // hardcoded replace
    String uid = "2alEvgIYNNQGf6GvJ5Jj5u5fXIB2";
    DatabaseReference database = FirebaseDatabase.getInstance().getReference("media").child(uid);
    ArrayList<DataClass> dataArray;

    // hardcoded replace with recycler view
    TextView date1;
    TextView date2;
    TextView date3;
    // hardcoded replace with recycler view
    ImageView img1;
    ImageView img2;
    ImageView img3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTimelineBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        dataArray = new ArrayList<>();
        // hardcoded replace with recycler view
        date1 = findViewById(R.id.date1);
        date2 = findViewById(R.id.date2);
        date3 = findViewById(R.id.date3);
        // hardcoded replace with recycler view
        img1 = findViewById(R.id.img1);
        img2 = findViewById(R.id.img2);
        img3 = findViewById(R.id.img3);


        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                    dataArray.add(dataClass);
                }

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

                // Update UI with sorted data
//                for (int i = 0; i < dataArray.size(); i++) {
//                    date.setText(dataArray.get(i).getDate());
//                    Glide.with(TimelineActivity.this).load(dataArray.get(i).getImageURL()).into(img1);
//                }
                // hardcoded replace
                date1.setText(dataArray.get(0).getDate());
                Glide.with(TimelineActivity.this).load(dataArray.get(0).getImageURL()).into(img1);
                date2.setText(dataArray.get(1).getDate());
                Glide.with(TimelineActivity.this).load(dataArray.get(1).getImageURL()).into(img2);
                date3.setText(dataArray.get(2).getDate());
                Glide.with(TimelineActivity.this).load(dataArray.get(2).getImageURL()).into(img3);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });







//        String storagePath = "media/" + uid + "/images/-OB7qo6YQBJq7ODPjIpa.png";
//        StorageReference storageRef = FirebaseStorage.getInstance().getReference(storagePath);



//        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//            @Override
//            public void onSuccess(Uri uri) {
//                // Load image into ImageView using Glide
//                Glide.with(TimelineActivity.this).load(uri).into(img1);
//                date.setText(dataArray.get(0).getDate());
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception exception) {
//                Toast.makeText(TimelineActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
//            }
//        });



//
//
//        photoLibrary = FirebaseDatabase.getInstance().getReference("media");
//        StorageReference images = FirebaseStorage.getInstance().getReference("media");
//        photoLibrary.child(uid).child("-OB7qo6YQBJq7ODPjIpa").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.isSuccessful()) {
//                    if(task.getResult().exists()) {
//                        Toast.makeText(TimelineActivity.this, "Library Successfully Found", Toast.LENGTH_LONG).show();
//                        DataSnapshot dataSnapshot = task.getResult();
//                        String dateValue = String.valueOf(dataSnapshot.child("date").getValue());
//                        date.setText(dateValue);
//
//                        // Fix using my adapters
//
//                        /*
//                        Uri imageURI = Uri.parse(String.valueOf(dataSnapshot.child("imageUrl").getValue()));
//
//                        img1.setImageURI(imageURI);
//
//                         */
//                        //String imageUrl = String.valueOf(dataSnapshot.child("imageUrl").getValue());
//                        //Glide.with(TimelineActivity.this).load(images.child(uid).child("images").child("-OB7qo6YQBJq7ODPjIpa.png")).into(img1);
//                    }
//                    // If the user does not have a library
//                    else {
//                        Toast.makeText(TimelineActivity.this, "Library Does Not Exist", Toast.LENGTH_LONG).show();
//                    }
//                }
//                // If failed to access database
//                else {
//                    Toast.makeText(TimelineActivity.this, "Failed to Read Data", Toast.LENGTH_LONG).show();
//                }
//
//            }
//        });

    }
}
