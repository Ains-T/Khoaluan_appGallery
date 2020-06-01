package com.example.galleryimgapp.Edit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.galleryimgapp.R;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.utils.ThumbnailItem;

import java.util.List;

public class ThumbnailsAdapter extends RecyclerView.Adapter<ThumbnailsAdapter.MyViewHolder>{

    private List<ThumbnailItem> thumbnailItemList;
    private ThumbnailsAdapterListener listener;
    private Context mContext;
    private int selectedIndex = 0;

    public ThumbnailsAdapter(List<ThumbnailItem> thumbnailItemList, ThumbnailsAdapterListener listener, Context mContext) {
        this.thumbnailItemList = thumbnailItemList;
        this.listener = listener;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.edit_thumbnail_list_item, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final ThumbnailItem thumbnailItem = thumbnailItemList.get(position);

        holder.mIvthumbnail.setImageBitmap(thumbnailItem.image);

        holder.mIvthumbnail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFilterSelected(thumbnailItem.filter);
                selectedIndex = position;
                notifyDataSetChanged();
            }
        });

        holder.mTvfilter.setText(thumbnailItem.filterName);

        if (selectedIndex == position) {
            holder.mTvfilter.setTextColor(ContextCompat.getColor(mContext,
                    R.color.filter_label_selected));
        }
        else {
            holder.mTvfilter.setTextColor(ContextCompat.getColor(mContext,
                    R.color.filter_label_normal));
        }
    }

    @Override
    public int getItemCount() {
        return thumbnailItemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView mIvthumbnail;
        TextView mTvfilter;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            mIvthumbnail = (ImageView) itemView.findViewById(R.id.edit_thumbnail_list_item_iv_thumbnail);
            mTvfilter = (TextView) itemView.findViewById(R.id.edit_thumbnail_list_item_tv_filterName);
        }


    }

    public interface ThumbnailsAdapterListener {
        void onFilterSelected(Filter filter);
    }
}
