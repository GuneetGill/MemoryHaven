package com.example.projectprototype;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RatingsAnalysisActivity extends AppCompatActivity {

    private LineChart lineChart;
    private DatabaseReference databaseReference;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_ratings);

        lineChart = findViewById(R.id.lineChart);

        // Get postId passed from ArchiveActivity
        postId = getIntent().getStringExtra("postId");

        // Validate postId
        if (postId == null || postId.isEmpty()) {
            Toast.makeText(this, "Post ID is missing", Toast.LENGTH_SHORT).show();
            finish(); // Close the activity if postId is invalid
            return;
        }

        // Initialize Firebase reference
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("media").child(userId).child(postId);

        // Load data and display chart
        loadRatingsData();
    }

    private void loadRatingsData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Entry> entries = new ArrayList<>();
                ArrayList<String> xAxisLabels = new ArrayList<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

                DataSnapshot ratingsSnapshot = snapshot.child("rating");
                DataSnapshot timeRatedSnapshot = snapshot.child("timeRated");

                if (!ratingsSnapshot.exists() || !timeRatedSnapshot.exists()) {
                    Toast.makeText(RatingsAnalysisActivity.this, "No data to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                for (DataSnapshot ratingSnap : ratingsSnapshot.getChildren()) {
                    String index = ratingSnap.getKey();
                    Float rating = ratingSnap.getValue(Float.class);

                    if (index != null && rating != null) {
                        String timeRated = timeRatedSnapshot.child(index).getValue(String.class);
                        if (timeRated != null) {
                            try {
                                Date date = dateFormat.parse(timeRated);
                                if (date != null) {
                                    xAxisLabels.add(dateFormat.format(date));
                                    entries.add(new Entry(entries.size(), rating));
                                }
                            } catch (ParseException e) {
                                Log.e("RatingsAnalysisActivity", "Error parsing date: " + e.getMessage());
                            }
                        }
                    }
                }

                if (entries.isEmpty()) {
                    Toast.makeText(RatingsAnalysisActivity.this, "No valid data to display", Toast.LENGTH_SHORT).show();
                    return;
                }

                LineDataSet dataSet = new LineDataSet(entries, "Ratings Over Time");
                dataSet.setColor(getResources().getColor(android.R.color.holo_blue_dark));
                dataSet.setValueTextColor(getResources().getColor(android.R.color.holo_blue_light));

                LineData lineData = new LineData(dataSet);

                // Configure the X-axis to display date labels
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < xAxisLabels.size()) {
                            return xAxisLabels.get(index);
                        } else {
                            return "";
                        }
                    }
                });

                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setGranularity(1f);
                xAxis.setLabelRotationAngle(-45f);

                // Configure the Y-axis to show integers only
                YAxis leftYAxis = lineChart.getAxisLeft();
                leftYAxis.setGranularity(1f); // Interval of 1
                leftYAxis.setAxisMinimum(1f); // Minimum value
                leftYAxis.setAxisMaximum(5f); // Maximum value
                leftYAxis.setLabelCount(5, true); // Exactly 5 labels
                leftYAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        return String.valueOf((int) value); // Cast to int for integer display
                    }
                });

                YAxis rightYAxis = lineChart.getAxisRight();
                rightYAxis.setEnabled(false); // Disable the right Y-axis

                // Set data and refresh chart
                lineChart.setData(lineData);
                lineChart.invalidate(); // Refresh chart

                lineChart.getAxisRight().setEnabled(false); // Disable right axis
                lineChart.getDescription().setEnabled(false); // Disable chart description
                lineChart.setExtraOffsets(5, 10, 5, 15); // Add padding for better visibility
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RatingsAnalysisActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
