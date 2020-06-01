package com.example.galleryimgapp.FullImage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.github.chrisbanes.photoview.PhotoView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class FullImageAdapter extends PagerAdapter {

    private Activity activity;
    private ArrayList<HashMap<String, String>> imgPaths;
    private LayoutInflater layoutInflater;

    public FullImageAdapter(Activity activity, ArrayList<HashMap<String, String>> imgPaths) {
        this.activity = activity;
        this.imgPaths = imgPaths;
    }

    @Override
    public int getCount() {
        return imgPaths.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((RelativeLayout) object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        PhotoView image;
        layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.full_image_layout,
                container, false);
        image = (PhotoView) view.findViewById(R.id.full_image_layout_pv_image);

        Glide.with(activity).load(new File(imgPaths.get(+position).get(Functions.KEY_PATH)))
                .into(image);

        //set an toolbar
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FullImage) activity).setToolbarView();
            }
        });

        ((ViewPager) container).addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
    }
}
