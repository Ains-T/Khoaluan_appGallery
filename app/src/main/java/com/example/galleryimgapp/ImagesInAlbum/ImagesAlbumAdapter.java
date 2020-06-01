package com.example.galleryimgapp.ImagesInAlbum;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class ImagesAlbumAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public ImagesAlbumAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
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
        ImgAlbumHolder holder = null;
        if (convertView == null) {
            holder = new ImgAlbumHolder();
            convertView = LayoutInflater.from(activity).inflate(
                    R.layout.images_album_items, parent, false);

            holder.images = (ImageView) convertView.findViewById(R.id.images_album_items_iv_image);
            convertView.setTag(holder);
        }
        else {
            holder = (ImgAlbumHolder) convertView.getTag();
        }

        holder.images.setId(position);

        HashMap<String,String> s = new HashMap<String, String>();
        s = data.get(position);

        try {
            Glide.with(activity).load(new File(s.get(Functions.KEY_PATH)))
                    .into(holder.images);
        }catch (Exception e){}
        return convertView;
    }

    static class ImgAlbumHolder {
        ImageView images;
    }
}
