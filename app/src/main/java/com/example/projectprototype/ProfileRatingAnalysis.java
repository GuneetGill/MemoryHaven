package com.example.projectprototype;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ProfileRatingAnalysis extends AppCompatActivity {

    private LineChart lineChart;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_analysis_rating_profile);

        lineChart = findViewById(R.id.lineChart);
        databaseReference = FirebaseDatabase.getInstance().getReference("media");

        setupLineChart();
        fetchAndPlotData();
    }

    private void setupLineChart() {
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Position labels at the bottom
        xAxis.setGranularity(1f); // Ensure consistent intervals for labels
        xAxis.setGranularityEnabled(true); // Ensure granularity is strictly followed

        // Format X-axis labels as dates
        xAxis.setValueFormatter(new ValueFormatter() {
            private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            @Override
            public String getFormattedValue(float value) {
                return dateFormat.format(new Date((long) value));
            }
        });

        xAxis.setLabelRotationAngle(-45); // Rotate labels diagonally
        lineChart.getAxisRight().setEnabled(false); // Disable right axis
        lineChart.getDescription().setEnabled(false); // Disable chart description
        lineChart.setExtraOffsets(5, 10, 5, 15); // Add padding for better visibility
    }

    private void fetchAndPlotData() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, ArrayList<Float>> ratingsByDate = new HashMap<>();

                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    for (DataSnapshot ratingSnapshot : postSnapshot.getChildren()) {
                        DataSnapshot ratingsNode = ratingSnapshot.child("rating");
                        DataSnapshot timeRatedNode = ratingSnapshot.child("timeRated");

                        if (ratingsNode.exists() && timeRatedNode.exists()) {
                            for (int i = 0; i < ratingsNode.getChildrenCount(); i++) {
                                Float rating = ratingsNode.child(String.valueOf(i)).getValue(Float.class);
                                String timestampStr = timeRatedNode.child(String.valueOf(i)).getValue(String.class);

                                if (rating != null && timestampStr != null) {
                                    try {
                                        // Extract date portion (dd/MM/yyyy) only
                                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                        Date date = sdf.parse(timestampStr.split(" ")[0]);
                                        if (date != null) {
                                            String formattedDate = sdf.format(date);

                                            // Add rating to the corresponding date
                                            ratingsByDate.putIfAbsent(formattedDate, new ArrayList<>());
                                            ratingsByDate.get(formattedDate).add(rating);
                                        }
                                    } catch (ParseException e) {
                                        Log.e("ProfileRatingAnalysis", "Date parsing error: " + e.getMessage());
                                    }
                                }
                            }
                        }
                    }
                }

                // Plot the aggregated data
                plotData(ratingsByDate);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProfileRatingAnalysis", "Database error: " + error.getMessage());
            }
        });
    }

    private void plotData(Map<String, ArrayList<Float>> ratingsByDate) {
        ArrayList<Entry> entries = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        for (Map.Entry<String, ArrayList<Float>> entry : ratingsByDate.entrySet()) {
            try {
                // Parse date and get timestamp
                Date date = sdf.parse(entry.getKey());
                if (date != null) {
                    long timestamp = date.getTime();

                    // Calculate average rating for the date
                    ArrayList<Float> ratings = entry.getValue();
                    float sum = 0f;
                    for (float rating : ratings) {
                        sum += rating;
                    }
                    float average = sum / ratings.size();

                    // Add to chart entries
                    entries.add(new Entry(timestamp, average));
                }
            } catch (ParseException e) {
                Log.e("ProfileRatingAnalysis", "Date parsing error: " + e.getMessage());
            }
        }

        // Sort entries by timestamp
        entries.sort((entry1, entry2) -> Float.compare(entry1.getX(), entry2.getX()));

        // Create dataset and apply to chart
        LineDataSet lineDataSet = new LineDataSet(entries, "Average Ratings");
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setValueTextSize(12f);

        LineData lineData = new LineData(lineDataSet);
        lineChart.setData(lineData);
        lineChart.invalidate(); // Refresh chart
    }
}
