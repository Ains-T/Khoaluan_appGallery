package com.example.galleryimgapp.FullImage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import android.app.ActionBar;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.galleryimgapp.Edit.EditImageActivity;
import com.example.galleryimgapp.Main.MainActivity;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class FullImage extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    Toolbar toolbar;
    LinearLayout bottomBar;
    Dialog dialog;
    ImageButton mIbCrop, mIbDel, mIbEdit;
    String imageFilePath;
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    boolean mToolbarVisibility = true;
    private ViewPager viewPager;
    private FullImageAdapter adapter;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_image);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        toolbar = findViewById(R.id.activity_full_image_tb_bar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.activity_full_image_vp_fullimage);
        Intent i = getIntent();
        int position = i.getIntExtra("position", 0);
        String title = i.getStringExtra("title");

        //ket noi du lieu hien thi hinh anh sau khi chon anh can xem
        imageList = (ArrayList<HashMap<String, String>>) i.getExtras().getSerializable("list");

        //truyen du lieu
        adapter = new FullImageAdapter(FullImage.this, imageList);
        viewPager.setAdapter(adapter);

        //set title anh va button tro lai trang truoc
        title = title.substring(title.lastIndexOf("/") + 1);
        if (title.length() > 15) {
            title = title.substring(0, 15).concat("...");
        }
        setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setCurrentItem(position);
        viewPager.addOnPageChangeListener(this);
        setBottomBar();
    }

    //set menu xem thong tin anh
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.info, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.info_info:
                setImageInfo();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //set Title
    void setTitle() {
        String path = imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH);
        //Toast.makeText(FullImage.this, path, Toast.LENGTH_SHORT).show();
        String title = "";
        title = path.substring(path.lastIndexOf("/") + 1);
        if (title.length() > 15) {
                title = title.substring(0,15).concat("...");
            }
        getSupportActionBar().setTitle(title);
    }

    //set su kien xem thong tin anh
    private void setImageInfo() {
        //khoi tao dialog va gan animation
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_info);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        //set thong tin anh
        String path = imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH);
        String title = "", time = "", width = "", height = "", imgsize = "";
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            ImageDetails details = new ImageDetails(path, exifInterface);
            File file = new File(path);
            Date date = new Date(file.lastModified());
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            title = details.getTitle();
            time = formatter.format(date).toString();
            width = details.getWidth() + "px";
            height = details.getHeight() + "px";
            imgsize = details.getImgsize() + " KB";
        } catch (IOException e) {
            e.printStackTrace();
        }
        TextView mTvPath, mTvTitle, mTvTime, mTvWidth, mTvHeight, mTvImgsize;
        mTvPath = dialog.findViewById(R.id.dialog_info_tv_path);
        mTvTitle = dialog.findViewById(R.id.dialog_info_tv_name);
        mTvTime = dialog.findViewById(R.id.dialog_info_tv_time);
        mTvWidth = dialog.findViewById(R.id.dialog_info_tv_width);
        mTvHeight = dialog.findViewById(R.id.dialog_info_tv_height);
        mTvImgsize = dialog.findViewById(R.id.dialog_info_tv_imgsize);

        mTvPath.setText(path);
        mTvPath.setSelected(true);
        mTvTitle.setText(title);
        mTvTitle.setSelected(true);
        mTvTime.setText(time);
        mTvTime.setSelected(true);
        mTvWidth.setText(width);
        mTvHeight.setText(height);
        mTvImgsize.setText(imgsize);

        //Toast.makeText(FullImage.this, time, Toast.LENGTH_SHORT).show();
        Button mBtBack = (Button) dialog.findViewById(R.id.dialog_info_bt_close);
        mBtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(true);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }


    // bottombar
    private void setBottomBar() {
        bottomBar = findViewById(R.id.activity_full_image_ll_bar);
        mIbCrop = findViewById(R.id.activity_full_image_ib_crop);
        mIbDel = findViewById(R.id.activity_full_image_ib_delete);
        mIbEdit = findViewById(R.id.activity_full_image_ib_edit);
        //set su kien xoa anh
        mIbDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteFunction();
            }
        });

        //set su kien cat anh
        mIbCrop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH);
                Uri inputUri = Uri.fromFile(new File(path));
                startCrop(inputUri);
            }
        });

        //set su kien edit
        mIbEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditFunction();
            }
        });
    }

    //su kien xoa anh
    private void DeleteFunction() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_delete);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        Button mBtYes, mBtNo;
        mBtYes = (Button) dialog.findViewById(R.id.dialog_delete_bt_yes);
        mBtNo = (Button) dialog.findViewById(R.id.dialog_delete_bt_no);

        mBtNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        mBtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImgFromPath();
            }
        });
        dialog.setCancelable(false);
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    private void deleteImgFromPath() {
        String path = imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH);
        File file = new File(path);

        //thiet lap phep chieu ID
        String[] projection = {MediaStore.Images.Media._ID};

        //thiet lap duong dan den tep phu hop
        String selection = MediaStore.Images.Media.DATA + " = ?";
        String[] selectionArgs = new String[]{file.getAbsolutePath()};

        //truy van ID phu hop voi duong dan tep
        Uri queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = getContentResolver();
        Cursor c = contentResolver.query(queryUri, projection, selection, selectionArgs, null);
        if (c.moveToNext()){
            //ta tim id. Xoa het anh trong thu muc dong thoi xoa luon file chua anh
            long id = c.getLong(c.getColumnIndexOrThrow(MediaStore.Images.Media._ID));
            Uri delUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
            contentResolver.delete(delUri, null, null);
            c.close();
            Toast.makeText(this, "Ảnh đã xoá", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            //khong tim thay anh trong file
            Toast.makeText(this, "File not found", Toast.LENGTH_SHORT).show();
        }
    }
    // end xoa anh


    //su kien cat anh
    private void startCrop(@NonNull Uri uri) {
        String timeStamp =
                new SimpleDateFormat("ddMMyyyy_HHmm",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMGCROP_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        imageFilePath = image.getAbsolutePath();
            UCrop uCrop = UCrop.of(uri, Uri.fromFile(image));
            uCrop.withAspectRatio(1, 1);
            uCrop.withMaxResultSize(450, 450);
            uCrop.withOptions(getCropOptions());
            uCrop.start(this);
    }

    private UCrop.Options getCropOptions() {
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(70);

        //CompressType
//        options.setCompressionFormat(Bitmap.CompressFormat.PNG);
//        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);

        //UI
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);

        //Colors
        options.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
//        options.setToolbarColor(getResources().getColor(R.color.colorPrimary));
//        options.set

        options.setToolbarTitle("Crop Image");
        return options;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        String path = imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH);
//        Uri inputUri = Uri.fromFile(new File(path));
//        if (requestCode == CODE_IMG_GALLERY && resultCode == RESULT_OK) {
//            Uri imgUri = data.getData();
//            if (imgUri != null) {
//                startCrop();
//            }
//        }
        if (requestCode == UCrop.REQUEST_CROP) {
            if (resultCode == RESULT_OK) {
                galleryAddPic();
            }
            else if (resultCode == RESULT_CANCELED) {
                deleteFile();
            }
        }
    }

    private void deleteFile() {
        try {
            File file = new File(imageFilePath);
            boolean deleted = file.delete();
        } catch (Exception e) {
        }
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(imageFilePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            this.sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    //end cat anh

    //su kien edit anh
    private void EditFunction() {
        Intent i = new Intent(this, EditImageActivity.class);
        i.putExtra("path", imageList.get(+viewPager.getCurrentItem()).get(Functions.KEY_PATH));
        startActivity(i);
    }
    //end edit anh

    //set an hien toolbar va bottombar
    void setToolbarView(){
        if (mToolbarVisibility) {
            getSupportActionBar().hide();
            bottomBar.setVisibility(View.GONE);
        }
        else {
            getSupportActionBar().show();
            bottomBar.setVisibility(View.VISIBLE);
        }
        mToolbarVisibility = !mToolbarVisibility;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle();
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
