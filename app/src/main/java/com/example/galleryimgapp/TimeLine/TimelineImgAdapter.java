package com.example.galleryimgapp.TimeLine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.galleryimgapp.FullImage.FullImage;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class TimelineImgAdapter extends RecyclerView.Adapter<TimelineImgAdapter.ViewHolder> {

    private Activity activity;
    private ArrayList<HashMap<String, String>> data;
    boolean pickintent = false;

    public TimelineImgAdapter(Activity activity, ArrayList<HashMap<String, String>> data) {
        this.activity = activity;
        this.data = data;
    }

    public interface ItemClickListener {
        void onClick(View view, int position,boolean isLongClick);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.timeline_items_image,
                parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mIvImg.setId(position);
        HashMap<String, String> hm = new HashMap<String, String>();
        hm = data.get(position);
        try {
            Glide.with(activity).load(new File(hm.get(Functions.KEY_PATH)))
                    .into(holder.mIvImg);
        }catch (Exception e){}

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                if (!pickintent){
                    Intent intent = new Intent(activity, FullImage.class);
                    String imgPath = data.get(+position).get(Functions.KEY_PATH);
                    intent.putExtra("title", imgPath);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", data);
                    intent.putExtras(bundle);
                    intent.putExtra("position", position);
                    activity.startActivity(intent);
                }
                else {
                    String imgPath = data.get(+position).get(Functions.KEY_PATH);
                    Uri imgUri = Uri.fromFile(new File(imgPath));
                    Intent intentResult = new Intent();
                    intentResult.setData(imgUri);
                    activity.setResult(Activity.RESULT_OK, intentResult);
                    activity.finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ItemClickListener itemClickListener;
        private ImageView mIvImg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvImg = (ImageView) itemView.findViewById(R.id.timeline_items_image_iv_image);

            itemView.setOnClickListener(this);
        }

        //set setter cho biến item
        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v, getAdapterPosition(), false);
        }
    }


}
