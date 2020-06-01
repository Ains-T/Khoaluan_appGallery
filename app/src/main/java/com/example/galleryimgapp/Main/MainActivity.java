package com.example.galleryimgapp.Main;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.galleryimgapp.Albums.AlbumFragment;
import com.example.galleryimgapp.Images.ImageFragment;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.TimeLine.TimelineFragment;
import com.nightonke.boommenu.BoomButtons.OnBMClickListener;
import com.nightonke.boommenu.BoomButtons.TextOutsideCircleButton;
import com.nightonke.boommenu.BoomMenuButton;

public class MainActivity extends AppCompatActivity {

    BoomMenuButton mBmb;
    private ImageFragment imageFragment;
    private AlbumFragment albumFragment;
    private TimelineFragment timelineFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set fragment
        imageFragment = new ImageFragment();
        albumFragment = new AlbumFragment();
        timelineFragment = new TimelineFragment();

        //set actionbar
        ActionBar mActionBar = getSupportActionBar();
        assert mActionBar != null;
        mActionBar.setDisplayShowCustomEnabled(false);
        mActionBar.setDisplayShowTitleEnabled(false);
        LayoutInflater mInflater = LayoutInflater.from(this);

        View actionbar = mInflater.inflate(R.layout.custom_actionbar, null);
        final TextView mTvTitle = actionbar.findViewById(R.id.custom_actionbar_tv_title);
        final ImageView mIvMenu = actionbar.findViewById(R.id.custom_actionbar_iv_menu);

        mTvTitle.setText("Images");
        setFragment(imageFragment);
        mActionBar.setCustomView(actionbar);
        mActionBar.setDisplayShowCustomEnabled(true);
        ((Toolbar) actionbar.getParent()).setContentInsetsAbsolute(0, 0);

        mBmb = (BoomMenuButton) actionbar.findViewById(R.id.custom_actionbar_bmb_left);
        TextOutsideCircleButton.Builder img = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.photos_white)
                .normalTextRes(R.string.image).textSize(14)
                .pieceColor(Color.WHITE).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        mTvTitle.setText("Images");
                        mIvMenu.setImageResource(R.drawable.ic_photos_white_1);
                        setFragment(imageFragment);
                    }
                });
        TextOutsideCircleButton.Builder albums = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.album_white)
                .normalTextRes(R.string.album).textSize(14)
                .pieceColor(Color.WHITE).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        mTvTitle.setText("Albums");
                        mIvMenu.setImageResource(R.drawable.ic_album_white_1);
                        setFragment(albumFragment);
                    }
                });
        TextOutsideCircleButton.Builder timeline = new TextOutsideCircleButton.Builder()
                .normalImageRes(R.drawable.time_white)
                .normalTextRes(R.string.timeline).textSize(14)
                .pieceColor(Color.WHITE).listener(new OnBMClickListener() {
                    @Override
                    public void onBoomButtonClick(int index) {
                        mTvTitle.setText("Timeline");
                        mIvMenu.setImageResource(R.drawable.ic_time_white_1);
                        setFragment(timelineFragment);
                    }
                });
        mBmb.addBuilder(img);
        mBmb.addBuilder(albums);
        mBmb.addBuilder(timeline);
    }

    private void setFragment (Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.activity_main_fl_container, fragment);
        fragmentTransaction.commit();
    }
}
