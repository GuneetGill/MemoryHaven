package com.example.projectprototype;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

public class UploadActivity extends AppCompatActivity {

    private FloatingActionButton uploadButton;
    private ImageView uploadImage;
    private EditText uploadCaption, uploadDate;
    private ProgressBar progressBar;
    private Button btnRecordAudio, btnUploadAudio;

    private Uri imageUri;
    private Uri audioUri; // New variable for selected audio URI
    private MediaRecorder mediaRecorder;
    private String audioFilePath;

    final private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");
    final private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadButton = findViewById(R.id.uploadButton);
        uploadCaption = findViewById(R.id.uploadCaption);
        uploadImage = findViewById(R.id.uploadImage);
        uploadDate = findViewById(R.id.uploadDate);
        btnRecordAudio = findViewById(R.id.btnRecordAudio);
        btnUploadAudio = findViewById(R.id.btnUploadAudio); // New button for uploading audio
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }

        btnRecordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaRecorder == null) {
                    startRecording();
                } else {
                    stopRecording();
                }
            }
        });

        btnUploadAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAudioFile();
            }
        });

        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            imageUri = data.getData();
                            uploadImage.setImageURI(imageUri);
                        } else {
                            Toast.makeText(UploadActivity.this, "No image Selected", Toast.LENGTH_SHORT).show();
                        }
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

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUri != null && (audioFilePath != null || audioUri != null)){
                    uploadToFirebase(imageUri);
                } else {
                    Toast.makeText(UploadActivity.this, "Please select an image and record/upload audio", Toast.LENGTH_SHORT).show();
                }
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener((item) -> {
            if(item.getItemId() == R.id.nav_home) {
                startActivity(new Intent(UploadActivity.this, SecondActivity.class));
            } else if (item.getItemId() == R.id.nav_profile) {
                startActivity(new Intent(UploadActivity.this, ProfileActivity.class));
            } else if (item.getItemId() == R.id.nav_archive) {
                startActivity(new Intent(UploadActivity.this, ArchiveActivity.class));
            }
            return true;
        });
    }

    private void startRecording() {
        audioFilePath = getExternalCacheDir().getAbsolutePath() + "/audio_record.3gp";
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            btnRecordAudio.setText("Stop Recording");
            Toast.makeText(this, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to start recording: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, "Recording in an invalid state: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            btnRecordAudio.setText("Record Audio");
            Toast.makeText(this, "Recording stopped", Toast.LENGTH_SHORT).show();
        } catch (RuntimeException stopException) {
            stopException.printStackTrace();
            Toast.makeText(this, "Failed to stop recording: " + stopException.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void selectAudioFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, 2); // Using request code 2 for audio selection
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == Activity.RESULT_OK && data != null) {
            audioUri = data.getData();
            Toast.makeText(this, "Audio selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase(Uri imageUri) {
        String caption = uploadCaption.getText().toString();
        String date = uploadDate.getText().toString();
        final StorageReference imageReference = storageReference.child("images/" + System.currentTimeMillis() + "." + getFileExtension(imageUri));
        final StorageReference audioReference = storageReference.child("audios/" + System.currentTimeMillis() + (audioFilePath != null ? ".3gp" : ".mp3"));

        imageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri imageUrl) {
                        uploadAudioToFirebase(audioReference, imageUrl.toString(), caption, date);
                    }
                });
            }
        }).addOnFailureListener(e -> Toast.makeText(UploadActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show());
    }

    private void uploadAudioToFirebase(StorageReference audioReference, String imageUrl, String caption, String date) {
        Uri finalAudioUri = audioUri != null ? audioUri : Uri.fromFile(new File(audioFilePath));

        audioReference.putFile(finalAudioUri).addOnSuccessListener(taskSnapshot ->
                audioReference.getDownloadUrl().addOnSuccessListener(audioUrl -> {
                    saveDataToDatabase(imageUrl, caption, date, audioUrl.toString());
                })
        ).addOnFailureListener(e -> Toast.makeText(UploadActivity.this, "Audio upload failed", Toast.LENGTH_SHORT).show());
    }

    private void saveDataToDatabase(String imageUrl, String caption, String date, String audioUrl) {
        DataClass dataClass = new DataClass(imageUrl, caption, date, audioUrl);
        String key = databaseReference.push().getKey();
        databaseReference.child(key).setValue(dataClass);
        progressBar.setVisibility(View.INVISIBLE);
        Toast.makeText(UploadActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(UploadActivity.this, SecondActivity.class));
        finish();
    }

    private String getFileExtension(Uri fileUri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted for audio recording", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied for audio recording", Toast.LENGTH_SHORT).show();
                btnRecordAudio.setEnabled(false);
            }
        }
    }
}
