    package com.example.projectprototype;

    import android.app.Activity;
    import android.content.ContentResolver;
    import android.content.Intent;
    import android.net.Uri;
    import android.os.Bundle;
    import android.view.View;
    import android.webkit.MimeTypeMap;
    import android.widget.Button;
    import android.widget.EditText;
    import android.widget.ImageView;
    import android.widget.ProgressBar;
    import android.widget.Toast;
    import android.app.DatePickerDialog;
    import android.widget.DatePicker;
    import java.util.Calendar;


    import androidx.activity.EdgeToEdge;
    import androidx.activity.result.ActivityResult;
    import androidx.activity.result.ActivityResultCallback;
    import androidx.activity.result.ActivityResultLauncher;
    import androidx.activity.result.contract.ActivityResultContracts;
    import androidx.annotation.NonNull;
    import androidx.appcompat.app.AppCompatActivity;
    import androidx.core.graphics.Insets;
    import androidx.core.view.ViewCompat;
    import androidx.core.view.WindowInsetsCompat;
    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;
    import androidx.fragment.app.FragmentTransaction;

    import com.example.projectprototype.databinding.ActivitySecondBinding;
    import com.example.projectprototype.databinding.ActivityUploadBinding;
    import com.google.android.gms.tasks.OnFailureListener;
    import com.google.android.gms.tasks.OnSuccessListener;
    import com.google.android.material.bottomnavigation.BottomNavigationView;
    import com.google.android.material.floatingactionbutton.FloatingActionButton;
    import com.google.firebase.database.DatabaseReference;
    import com.google.firebase.database.FirebaseDatabase;
    import com.google.firebase.storage.FirebaseStorage;
    import com.google.firebase.storage.OnProgressListener;
    import com.google.firebase.storage.StorageReference;
    import com.google.firebase.storage.UploadTask;

    public class UploadActivity extends AppCompatActivity {

        // The uploading page
        ActivityUploadBinding binding;
        private FloatingActionButton uploadButton;
        private ImageView uploadImage;
        EditText uploadCaption;
        ProgressBar progressBar;
        Button datePickerButton;
        Button audioPickerButton;
        private Uri imageUri;
        private Uri audioUri;
        private int year, month, day;
        private ActivityResultLauncher<Intent> audioResultLauncher;
        final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
        final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_upload);
            uploadButton = findViewById(R.id.uploadButton);
            uploadCaption = findViewById(R.id.uploadCaption);
            uploadImage = findViewById(R.id.uploadImage);
            progressBar = findViewById(R.id.progressBar);
            datePickerButton = findViewById(R.id.select_date_button);
            audioPickerButton = findViewById(R.id.attach_audio_file_button);
            progressBar.setVisibility(View.INVISIBLE);


            ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if(result.getResultCode() == Activity.RESULT_OK){
                                Intent data = result.getData();
                                imageUri = data.getData();
                                uploadImage.setImageURI(imageUri);

                            }else {
                                Toast.makeText(UploadActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

            audioResultLauncher = registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                                audioUri = result.getData().getData();
                                String fileName = audioUri.getLastPathSegment();
                                audioPickerButton.setText(fileName);
                                Toast.makeText(UploadActivity.this, "Audio file attached", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(UploadActivity.this, "No audio file selected", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );

            audioPickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent audioPickerIntent = new Intent();
                    audioPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                    audioPickerIntent.setType("audio/*");
                    audioResultLauncher.launch(audioPickerIntent);
                }
            });

            uploadImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent photoPicker = new Intent();
                    photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                    photoPicker.setType("image/*");
                    activityResultLauncher.launch(photoPicker);
                }
            });

            datePickerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the current date
                    final Calendar calendar = Calendar.getInstance();
                    year = calendar.get(Calendar.YEAR);
                    month = calendar.get(Calendar.MONTH);
                    day = calendar.get(Calendar.DAY_OF_MONTH);

                    // Open the DatePickerDialog
                    DatePickerDialog datePickerDialog = new DatePickerDialog(UploadActivity.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                    // Store selected date
                                    year = selectedYear;
                                    month = selectedMonth;
                                    day = selectedDay;

                                    // Update button text with selected date in YEAR MONTH DAY format
                                    String dateString = year + "-" + (month + 1) + "-" + day;
                                    datePickerButton.setText(dateString);
                                }
                            }, year, month, day);
                    datePickerDialog.show();
                }
            });

            uploadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(imageUri != null){
                        uploadToFirebase(imageUri);
                    }
                    else{
                        Toast.makeText(UploadActivity.this, "Please select image", Toast.LENGTH_SHORT).show();
                    }
                }
            });


    //        binding = ActivityUploadBinding.inflate(getLayoutInflater());
    //        setContentView(binding.getRoot());
            BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
            bottomNavigationView.setOnNavigationItemSelectedListener((item)->{
                if(item.getItemId() == R.id.nav_home){ //if user click home icon, switch to home page
                    Intent intent = new Intent(UploadActivity.this, SecondActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_profile) { //if user click profile icon, switch to profile page
                    Intent intent = new Intent(UploadActivity.this,ProfileActivity.class);
                    startActivity(intent);
                } else if (item.getItemId() == R.id.nav_archive) { //if user click archive icon, switch to archive page
                    Intent intent = new Intent(UploadActivity.this, ArchiveActivity.class);
                    startActivity(intent);

                }


                return true;

            });

        }
        private void uploadToFirebase(Uri uri){
            String caption = uploadCaption.getText().toString();
            final StorageReference imageReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(uri));


            imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            DataClass dataClass = new DataClass(uri.toString(),caption);
                            String key = databaseReference.push().getKey();
                            databaseReference.child(key).setValue(dataClass);
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(UploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(UploadActivity.this, SecondActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    progressBar.setVisibility(View.VISIBLE);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(UploadActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        private String getFileExtension(Uri fileUri){
            ContentResolver contentResolver = getContentResolver();
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
        }


    }