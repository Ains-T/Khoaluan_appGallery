package com.example.galleryimgapp.Images;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.arch.core.util.Function;

import com.bumptech.glide.Glide;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImageAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public ImageAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data = data;
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
        ImageHolder holder = null;
        if (convertView == null){
            holder = new ImageHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.image_items, parent, false);

            holder.images = (ImageView) convertView.findViewById(R.id.image_items_iv_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ImageHolder) convertView.getTag();
        }
        holder.images.setId(position);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm = data.get(position);
        try {
            Glide.with(activity).load(new File(hm.get(Functions.KEY_PATH)))
                    .into(holder.images);
        }catch (Exception e){}
        return convertView;
    }

    static class ImageHolder{
        ImageView images;
    }
}
