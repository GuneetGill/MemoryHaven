package com.example.projectprototype;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<DataClass> dataList;
    private ArrayList<String> mediaIdList;
    private Context context;

    private SharedPreferences sharedPreferences;
    private boolean isUidLogin;

    public MyAdapter(ArrayList<DataClass> dataList, ArrayList<String> mediaIdList, Context context, SharedPreferences sharedPreferences) {
        this.dataList = dataList;
        this.mediaIdList = mediaIdList;
        this.context = context;
        this.sharedPreferences = sharedPreferences;
        this.isUidLogin = sharedPreferences.getBoolean("isUidLogin", false);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass data = dataList.get(position);

        // Load the image, set caption, date, etc.
        Glide.with(context).load(data.getImageURL()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(data.getCaption());
        holder.recyclerDate.setText(data.getDate());

        // Set up audio playback
        holder.playAudioButton.setOnClickListener(v -> {
            String audioUrl = data.getAudioUrl();
            if (audioUrl != null && !audioUrl.isEmpty()) {
                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

                try {
                    mediaPlayer.setDataSource(audioUrl);
                    mediaPlayer.prepare();
                    mediaPlayer.start();
                    Toast.makeText(context, "Playing audio", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    Toast.makeText(context, "Failed to play audio", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }

                mediaPlayer.setOnCompletionListener(mp -> {
                    mediaPlayer.release();
                    Toast.makeText(context, "Audio finished", Toast.LENGTH_SHORT).show();
                });
            } else {
                Toast.makeText(context, "No audio available", Toast.LENGTH_SHORT).show();
            }
        });

        // Pass the Firebase key to PostRatingActivity
        holder.recyclerImage.setOnClickListener(v -> {
            if(!isUidLogin) {
                String mediaId = mediaIdList.get(position); // Get the Firebase key for this item
                Intent intent = new Intent(context, PostRatingActivity.class);
                intent.putExtra("MEDIA_ID", mediaId); // Pass the unique Firebase key
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView recyclerImage;
        TextView recyclerCaption, recyclerDate;
        Button playAudioButton;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            recyclerDate = itemView.findViewById(R.id.recyclerDate);
            playAudioButton = itemView.findViewById(R.id.btnPlayAudio);
        }
    }
}
