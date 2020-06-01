package com.example.galleryimgapp.ImagesInAlbum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.galleryimgapp.FullImage.FullImage;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.example.galleryimgapp.Utils.MapComparator;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

public class ImagesAlbum extends AppCompatActivity {

    String album_name = "";
    GridView gridView;
    ImagesAlbumAdapter adapter;
    LoadImgAlbum loadImgAlbum;
    Dialog dialog;
    ArrayList<HashMap<String, String>> imagesList = new ArrayList<HashMap<String, String>>();
    boolean pickintent = false;
    static final int REQUEST_PERMISSION_KEY = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_album);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

        gridView = (GridView) findViewById(R.id.activity_images_album_gv_imgalbum);

        //lay du lieu tu AlbumFragment
        Intent intent = getIntent();
        album_name = intent.getStringExtra("name_album");
        pickintent = intent.getExtras().getBoolean("pickintent", false);

        //set title va tro lai trang truoc
        setTitle(album_name);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360){
            dp = (dp - 17) / 2;
            float px = Functions.convertDpToPixel(dp, getApplicationContext());
            gridView.setColumnWidth(Math.round(px));
        }

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Functions.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
    }

    //set Title
    public void setTitle(String title){
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView textView = new TextView(this);
        textView.setText(title);
        textView.setTextSize(20);
        textView.setTypeface(null, Typeface.BOLD);
        textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(getResources().getColor(R.color.white));
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(textView);
    }


    //gan menu sap xep anh
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //set su kien tro ve và su kien sap xep anh
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.sort_sort:
                setReorder();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //su kien sap xep anh
    private void setReorder() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_sort);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        Window window = dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.getAttributes().windowAnimations = R.style.DialogAnimation;

        //sort
        final RadioGroup mRgItems = dialog.findViewById(R.id.dialog_sort_rg_sort);
        final RadioGroup mRgMethod = dialog.findViewById(R.id.dialog_sort_rg_sort2);
        Button mBtSort = dialog.findViewById(R.id.dialog_sort_bt_sort);

        mBtSort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String order = "desc";
                switch (mRgMethod.getCheckedRadioButtonId()){
                    case R.id.dialog_sort_rb_asc:
                        order = "asc";
                        break;
                    case R.id.dialog_sort_rb_desc:
                        order = "desc";
                        break;
                    default:
                        break;
                }

                switch (mRgItems.getCheckedRadioButtonId()){
                    case R.id.dialog_sort_rb_name:
                        String name = Functions.KEY_PATH;
                        name = name.substring(name.lastIndexOf("/") + 1);
//                        Toast.makeText(getContext(), name, Toast.LENGTH_SHORT).show();
                        Collections.sort(imagesList, new MapComparator(name, order));
                        break;
                    case R.id.dialog_sort_rb_time:
                        if (order.toLowerCase().contentEquals("asc")) {
                            Collections.sort(imagesList, new Comparator<HashMap<String, String>>() {
                                @Override
                                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    Date date1 = null, date2 = null;
                                    try {
                                        date1 = formatter.parse(o1.get(Functions.KEY_TIMESTAMP));
                                        date2 = formatter.parse(o2.get(Functions.KEY_TIMESTAMP));


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    return date1.compareTo(date2);
                                }
                            });
                        }
                        else {
                            Collections.sort(imagesList, new Comparator<HashMap<String, String>>() {
                                @Override
                                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                                    Date date1 = null, date2 = null;
                                    try {
                                        date1 = formatter.parse(o1.get(Functions.KEY_TIMESTAMP));
                                        date2 = formatter.parse(o2.get(Functions.KEY_TIMESTAMP));


                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    return date2.compareTo(date1);
                                }
                            });
                        }
                        break;
                    case R.id.dialog_sort_rb_imgsize:
                        if (order.toLowerCase().contentEquals("asc")) {
                            Collections.sort(imagesList, new Comparator<HashMap<String, String>>() {
                                @Override
                                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                    int a = Integer.valueOf(o1.get(Functions.KEY_IMGSIZE));
                                    int b = Integer.valueOf(o2.get(Functions.KEY_IMGSIZE));

                                    return a > b ? 1 : -1;
                                }
                            });
                        }
                        else{
                            Collections.sort(imagesList, new Comparator<HashMap<String, String>>() {
                                @Override
                                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                    int a = Integer.valueOf(o1.get(Functions.KEY_IMGSIZE));
                                    int b = Integer.valueOf(o2.get(Functions.KEY_IMGSIZE));

                                    return b > a ? 1 : -1;
                                }
                            });
                        }
                        break;
                    default:
                        break;
                }
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        dialog.setCancelable(true);
        window.setLayout(android.app.ActionBar.LayoutParams.WRAP_CONTENT,
                android.app.ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
    }

    //load anh trong album
    class LoadImgAlbum extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            imagesList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            imagesList.clear();

            String xml = "";
            String path = null;
            String album = null;
            String timestamp = null;
            String imgsize = null;

            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getContentResolver().query(uriExternal,
                    projection, "bucket_display_name = \"" + album_name + "\"",
                    null, null);
            Cursor cursorInternal = getContentResolver().query(uriInternal,
                    projection, "bucket_display_name = \"" + album_name + "\"",
                    null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()) {
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));

                File file = new File(path);
                Date date = new Date(file.lastModified());
                DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                timestamp = formatter.format(date).toString();
                imgsize = String.valueOf((int) (file.length() / 1024));
                imagesList.add(Functions.mappingInBox(album, path, timestamp, null, imgsize));
            }
            cursor.close();

            Collections.sort(imagesList, new Comparator<HashMap<String, String>>() {
                @Override
                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                    DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date1 = null, date2 = null;
                    try {
                        date1 = formatter.parse(o1.get(Functions.KEY_TIMESTAMP));
                        date2 = formatter.parse(o2.get(Functions.KEY_TIMESTAMP));


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return date2.compareTo(date1);
                }
            });
            return xml;
        }

        @Override
        protected void onPostExecute(String xml) {
            adapter = new ImagesAlbumAdapter(ImagesAlbum.this, imagesList);
            gridView.setAdapter(adapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!pickintent) {
                        Intent intent = new Intent(ImagesAlbum.this, FullImage.class);
                        Bundle bundle = new Bundle();
                        String imgPath = imagesList.get(+position).get(Functions.KEY_PATH);
                        intent.putExtra("title", imgPath);
                        bundle.putSerializable("list", imagesList);
                        intent.putExtras(bundle);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                    else {
                        String imgPath = imagesList.get(+position).get(Functions.KEY_PATH);
                        Uri imgUri = Uri.fromFile(new File(imgPath));
                        Intent intentResult = new Intent();
                        intentResult.setData(imgUri);
                        setResult(Activity.RESULT_OK, intentResult);
                        finish();
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!Functions.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
        else {
            loadImgAlbum = new LoadImgAlbum();
            loadImgAlbum.execute();
        }
    }
}
