package com.example.galleryimgapp.Albums;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AlbumAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public AlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
        AlbumHolder holder = null;
        if(convertView == null){
            holder = new AlbumHolder();
            convertView = LayoutInflater.from(activity).inflate(R.layout.album_items,
                    parent, false);
            holder.albumImage = (ImageView) convertView.findViewById(R.id.album_items_iv_album);
            holder.album_count = (TextView) convertView.findViewById(R.id.album_items_tv_album_count);
            holder.album_title = (TextView) convertView.findViewById(R.id.album_items_tv_album_title);

            convertView.setTag(holder);
        }
        else {
            holder = (AlbumHolder) convertView.getTag();
        }

        holder.albumImage.setId(position);
        holder.album_count.setId(position);
        holder.album_title.setId(position);

        HashMap<String,String> s = new HashMap<String, String>();
        s= data.get(position);
        try {
            holder.album_title.setText(s.get(Functions.KEY_ALBUM));
            holder.album_count.setText(s.get(Functions.KEY_COUNT));

            Glide.with(activity).load(new File(s.get(Functions.KEY_PATH)))
                    .into(holder.albumImage);
        }catch (Exception e){}
        return convertView;
    }

    static class AlbumHolder {
        ImageView albumImage;
        TextView album_count, album_title;
    }
}
