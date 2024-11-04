package com.example.projectprototype;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MyAdapter2 extends BaseAdapter {
    private ArrayList<DataClass> dataList2;
    private Context context2;

    LayoutInflater layoutInflater;

    public MyAdapter2(ArrayList<DataClass> dataList, Context context) {
        this.dataList2 = dataList;
        this.context2 = context;
    }

    @Override
    public int getCount() {
        return dataList2.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList2.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertview, ViewGroup parent) {
        if(convertview == null){
            convertview = LayoutInflater.from(context2).inflate(R.layout.grid_item,parent,false);
        }
        ImageView imageView = convertview.findViewById(R.id.gridImage);
        DataClass dataClass = dataList2.get(i);

        Glide.with(context2).load(dataClass.getImageURL()).into(imageView);
        return convertview;
    }
}
