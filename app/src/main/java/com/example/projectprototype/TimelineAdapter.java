package com.example.projectprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

//
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DataClass> dataClassList;

    public TimelineAdapter(Context context, ArrayList<DataClass> dataClassList) {
        this.context = context;
        this.dataClassList = dataClassList;
    }

    @NonNull
    @Override
    public TimelineAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflates the layout (giving a look to recycler view items)

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_timeline, parent, false);

        return new TimelineAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineAdapter.MyViewHolder holder, int position) {
        // Assigns values to the views created in the recycler view
        // based on the position of the recycler view

        DataClass data = dataClassList.get(position);
        holder.textView.setText(data.getDate());
        Glide.with(context).load(data.getImageURL()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return dataClassList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // Grabs the views from recycler view similar to onCreate

        ImageView imageView;
        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.imageView);
            textView = itemView.findViewById(R.id.textView);

        }
    }

}
