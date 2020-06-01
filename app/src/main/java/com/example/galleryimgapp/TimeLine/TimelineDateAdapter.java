package com.example.galleryimgapp.TimeLine;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryimgapp.FullImage.FullImage;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.example.galleryimgapp.Utils.SpacesItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TimelineDateAdapter extends BaseAdapter {


    TimelineImgAdapter adapter;

    boolean pickintent = false;
    private Activity activity;
    private ArrayList<String> data;
    private ArrayList<HashMap<String, String>> imagesList;
    ArrayList<HashMap<String, String>> images;

    static final int REQUEST_PERMISSION_KEY = 1;

    public TimelineDateAdapter(Activity activity, ArrayList<String> data, ArrayList<HashMap<String, String>> imagesList) {
        this.activity = activity;
        this.data = data;
        this.imagesList = imagesList;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TLDateHolder holder = null;
        if (convertView == null) {
            holder = new TLDateHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.timeline_items_date,
                    parent, false);

            holder.mTvTime = (TextView) convertView.findViewById(R.id.timeline_items_date_tv_date);
            holder.mRvTime = (RecyclerView) convertView.findViewById(R.id.timeline_items_date_rv_images);
            convertView.setTag(holder);
        }
        else {
            holder = (TLDateHolder) convertView.getTag();
        }

        //recyclerview
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false);
        holder.mRvTime.setLayoutManager(layoutManager);

//        holder.mRvTime.setItemAnimator(new DefaultItemAnimator());
//        int space = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
//                activity.getResources().getDisplayMetrics());
//        holder.mRvTime.addItemDecoration(new SpacesItemDecoration(space));

//        int iDisplayWidth = convertView.getResources().getDisplayMetrics().widthPixels;
//        Resources resources = convertView.getContext().getApplicationContext().getResources();
//        DisplayMetrics metrics = resources.getDisplayMetrics();
//        float dp = iDisplayWidth / (metrics.densityDpi / 160f);
//
//        if (dp < 360){
//            dp = (dp - 17) / 2;
//            float px = Functions.convertDpToPixel(dp, convertView.getContext().getApplicationContext());
//            Recy
//        }

        images = new ArrayList<HashMap<String, String>>();
        String timestamp = null;
        int cout = 0;
        for (int  i = 0; i < imagesList.size(); i++) {
            timestamp = imagesList.get(i).get(Functions.KEY_TIMESTAMP);
            String a = timestamp.substring(0, timestamp.indexOf(" "));
            if (a.equals(data.get(position))){
                cout++;
                images.add(imagesList.get(i));
            }
        }
        adapter = new TimelineImgAdapter(activity, images);
        holder.mTvTime.setText("- " + data.get(position) + "  (" + cout + " photos) -");
        holder.mRvTime.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return convertView;
    }

    static class TLDateHolder {
        TextView mTvTime;
        RecyclerView mRvTime;
    }


}
