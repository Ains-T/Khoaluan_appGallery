package com.example.galleryimgapp.Albums;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.galleryimgapp.ImagesInAlbum.ImagesAlbum;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.Utils.Functions;
import com.example.galleryimgapp.Utils.MapComparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class AlbumFragment extends Fragment {

    GridView gridView;
    ArrayList<HashMap<String, String>> listAlbum = new ArrayList<HashMap<String, String>>();
    AlbumAdapter adapter;
    LoadAlbum loadAlbum;
    String imageFilePath;

    boolean pickintent = false;
    static final int REQUEST_PERMISSION_KEY = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PICK_IMAGE = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.album_fragment, container, false);

        setHasOptionsMenu(true);

        gridView = (GridView) view.findViewById(R.id.album_fragment_gv_albums);

        int iDisplayWidth = getResources().getDisplayMetrics().widthPixels;
        Resources resources = getContext().getApplicationContext().getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = iDisplayWidth / (metrics.densityDpi / 160f);

        if (dp < 360){
            dp = (dp - 17) / 2;
            float px = Functions.convertDpToPixel(dp, getContext().getApplicationContext());
            gridView.setColumnWidth(Math.round(px));
        }
        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Functions.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS,
                    REQUEST_PERMISSION_KEY);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.camera, menu);
    }

    //set su kien may anh
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.camera_camera:
                setCamera();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //su kien may anh
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
                gridView.invalidateViews();
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

    //Load Album
    class LoadAlbum extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listAlbum.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            listAlbum.clear();

            String xml = "";
            String path = null;
            String album = null;
            String timestamp = null;
            String countPhoto = null;
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
                listAlbum.add(Functions.mappingInBox(album, path, timestamp, countPhoto, null));
            }
            cursor.close();
            Collections.sort(listAlbum, new MapComparator(Functions.KEY_TIMESTAMP, "desc"));
            return xml;
        }

        @Override
        protected void onPostExecute(String s) {
            adapter = new AlbumAdapter(getActivity(), listAlbum);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getContext(), ImagesAlbum.class);
                    intent.putExtra("name_album", listAlbum.get(+position).get(Functions.KEY_ALBUM));
                    if (!pickintent){
                        startActivity(intent);
                    }
                    else {
                        intent.putExtra("pickintent", pickintent);
                        startActivityForResult(intent, REQUEST_PICK_IMAGE);
                    }
                }
            });
        }
    }

    //
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_KEY: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadAlbum = new LoadAlbum();
                    loadAlbum.execute();
                }
                else {
                    Toast.makeText(getContext(), "Bạn phải xác nhạn quyền.", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Functions.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(),
                    PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
        else {
            loadAlbum = new LoadAlbum();
            loadAlbum.execute();
        }
    }
}
