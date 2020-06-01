package com.example.galleryimgapp.TimeLine;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.example.galleryimgapp.FullImage.FullImage;
import com.example.galleryimgapp.Images.ImageAdapter;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.example.galleryimgapp.Utils.MapComparator;
import com.example.galleryimgapp.Utils.SpacesItemDecoration;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Random;

public class TimelineFragment extends Fragment {

    ListView mLvView;
    Animation in, out;
    ViewFlipper viewFlipper;
    TimelineDateAdapter adapter;
    String imageFilePath;
    LoadAll loadAll;
    TextView mTvTime;
    ArrayList<String> timeListTemp;
    ArrayList<HashMap<String, String>> imagesList = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> albumList = new ArrayList<HashMap<String, String>>();

    static final int REQUEST_PERMISSION_KEY = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PICK_IMAGE = 200;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_fragment, container, false);

        viewFlipper = (ViewFlipper) view.findViewById(R.id.timeline_fragment_vf_time);
        mTvTime = (TextView) view.findViewById(R.id.timeline_fragment_tv_date);

        mLvView = (ListView) view.findViewById(R.id.timeline_fragment_lv_time);

        in = AnimationUtils.loadAnimation(getContext(), R.anim.fade_in);
        out = AnimationUtils.loadAnimation(getContext(), R.anim.fade_out);

        viewFlipper.setInAnimation(in);
        viewFlipper.setOutAnimation(out);
        viewFlipper.setFlipInterval(4000);
        viewFlipper.setAutoStart(true);

        setHasOptionsMenu(true);

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Functions.hasPermissions(getContext(), PERMISSIONS)) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS,
                    REQUEST_PERMISSION_KEY);
        }

        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.camera, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.camera_camera:
                setCamera();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //set su kien may anh
    private void setCamera(){
        Dexter.withContext(getActivity())
                .withPermission(Manifest.permission.CAMERA)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        openCamera();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        makeToast();
                    }

                    void makeToast() {
                        Toast.makeText(getActivity(), "Bạn phải xác nhận quyền truy cập camera", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();
    }

    private void openCamera() {
        if (isDeviceSupportCamera()) {
            openCameraIntent();
        } else
            Toast.makeText(getActivity(), "Thiết bị này không hỗ trợ camera", Toast.LENGTH_SHORT).show();
    }

        //kiem tra camera cua thiet bi
    private boolean isDeviceSupportCamera() {
        if (getContext().getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA_ANY)) {
            // thiết bị này có camera
            return true;
        } else {
            // không có camera trong thiết bị này
            return false;
        }
    }

    private void openCameraIntent() {
        Intent pictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        if (pictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            //Tao file chua anh
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {}
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.galleryimgapp.provider", photoFile);
                pictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        photoURI);
                try {
                    startActivityForResult(pictureIntent, REQUEST_CAPTURE_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp =
                new SimpleDateFormat("ddMMyyyy_HHmm",
                        Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + "_";
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "Camera");

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        imageFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            if (resultCode == Activity.RESULT_CANCELED) {
                deleteFile();
            }
            else if (resultCode == Activity.RESULT_OK){
                galleryAddPic();
                adapter.notifyDataSetChanged();
                mLvView.invalidateViews();

                TimelineFragment fragment = (TimelineFragment)
                        getFragmentManager().findFragmentById(R.id.activity_main_fl_container);

                getFragmentManager().beginTransaction()
                        .detach(fragment)
                        .attach(fragment)
                        .commit();

            }
        }
        if (requestCode == REQUEST_PICK_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intentResult = new Intent();
                intentResult.setData(data.getData());
                getActivity().setResult(Activity.RESULT_OK,intentResult);
                getActivity().finish();

            }
        }
    }

    private void galleryAddPic() {
        try {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            File f = new File(imageFilePath);
            Uri contentUri = Uri.fromFile(f);
            mediaScanIntent.setData(contentUri);
            getContext().sendBroadcast(mediaScanIntent);
        } catch (Exception e) {
            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteFile() {
        try {
            File file = new File(imageFilePath);
            boolean deleted = file.delete();
        } catch (Exception e) {}
    }
    //het may anh

    class LoadAll extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            albumList.clear();
            imagesList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            albumList.clear();
            imagesList.clear();

            String xml = "";
            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
            String imgSize = null;
            Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;
            String[] projection = {MediaStore.MediaColumns.DATA,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                    MediaStore.MediaColumns.DATE_MODIFIED};

            Cursor cursorExternal = getContext().getContentResolver().query(uriExternal,
                    projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);
            Cursor cursorInternal = getContext().getContentResolver().query(uriInternal,
                    projection, "_data IS NOT NULL) GROUP BY (bucket_display_name",
                    null, null);

            Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

            while (cursor.moveToNext()){
                path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                album = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                timestamp = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED));
                countPhoto = Functions.getCount(getContext().getApplicationContext(), album);
                albumList.add(Functions.mappingInBox(album, path, timestamp, countPhoto, null));
            }
            cursor.close();
            for (int i = 0; i < albumList.size(); i++){
                Cursor cursorEx = getContext().getContentResolver().query(uriExternal,
                        projection, "bucket_display_name = \"" + albumList.get(i).get(Functions.KEY_ALBUM) + "\"",
                        null, null);
                Cursor cursorIn = getContext().getContentResolver().query(uriInternal,
                        projection, "bucket_display_name = \"" + albumList.get(i).get(Functions.KEY_ALBUM) + "\"",
                        null, null);

                Cursor cursor1 = new MergeCursor(new Cursor[]{cursorEx, cursorIn});
                while (cursor1.moveToNext()){
                    album = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                    if (album.contentEquals("Screenshots") || album.contentEquals("Camera")
                            || album.contentEquals("Pictures") || album.contentEquals("Download")) {
                        path = cursor1.getString(cursor1.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA));
                        File file = new File(path);
                        Date date = new Date(file.lastModified());
                        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        timestamp = formatter.format(date).toString();
                        imgSize = String.valueOf((int) (file.length() / 1024));
                        if (Integer.valueOf(imgSize) != 0) {
                            imagesList.add(Functions.mappingInBox(album, path, timestamp, null, imgSize));
                        }
                    }
                    else {
                        continue;
                    }
                }
                cursor1.close();
            }
            //sap xep anh theo time giam dan
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
//            int a = 0;

            timeListTemp = new ArrayList<String>();
            for (int i = 0; i < imagesList.size(); i++) {
                Random random = new Random();
                final int n = random.nextInt(imagesList.size());
                ImageView mIvViewFl = new ImageView(getContext());
                Glide.with(getActivity()).load(new File(imagesList.get(n)
                        .get(Functions.KEY_PATH))).into(mIvViewFl);
                String temp = imagesList.get(i).get(Functions.KEY_TIMESTAMP);
                String a = temp.substring(0, temp.indexOf(" "));
                timeListTemp.add(a);
                viewFlipper.addView(mIvViewFl);
            }
            ArrayList<String> timeList = new ArrayList<String>();
            for (String element : timeListTemp) {
                if (!timeList.contains(element)) {
                    timeList.add(element);
                }
            }

            String pathfirst = imagesList.get(0).get(Functions.KEY_TIMESTAMP);
            String pathlast = imagesList.get(imagesList.size() - 1).get(Functions.KEY_TIMESTAMP);
            String time = "From: " + pathlast.substring(0, pathlast.indexOf(" ")) + " to " + pathfirst.substring(0, pathfirst.indexOf(" "));
            mTvTime.setText(time);

//            mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,
//                    StaggeredGridLayoutManager.VERTICAL));
            adapter = new TimelineDateAdapter(getActivity(), timeList, imagesList);
            mLvView.setAdapter(adapter);
        }
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAll = new LoadAll();
                    loadAll.execute();
                }
                else {
                    Toast.makeText(getContext(), "Bạn phải xác nhạn quyền.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    //Hien thi tat ca anh
    @Override
    public void onResume() {
        super.onResume();

//        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                Manifest.permission.READ_EXTERNAL_STORAGE};
//        if(!Functions.hasPermissions(getContext(), PERMISSIONS)){
//            ActivityCompat.requestPermissions(getActivity(),
//                    PERMISSIONS, REQUEST_PERMISSION_KEY);
//        }
//        else {
        loadAll = new LoadAll();
        loadAll.execute();
//        }
    }
}
