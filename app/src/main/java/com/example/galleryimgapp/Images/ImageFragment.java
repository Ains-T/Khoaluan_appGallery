package com.example.galleryimgapp.Images;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.galleryimgapp.FullImage.FullImage;
import com.example.galleryimgapp.R;
import com.example.galleryimgapp.TimeLine.TimelineFragment;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class ImageFragment extends Fragment {

    GridView gridView;
    ArrayList<HashMap<String, String>> listFiles = new ArrayList<HashMap<String, String>>();
    ArrayList<HashMap<String, String>> imageList = new ArrayList<HashMap<String, String>>();
    LoadAll loadAll;
    ImageAdapter adapter;
    Dialog dialog;
    String imageFilePath;

    boolean pickintent = false;
    static final int REQUEST_PERMISSION_KEY = 1;
    private static final int REQUEST_CAPTURE_IMAGE = 100;
    private static final int REQUEST_PICK_IMAGE = 200;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.image_fragment, container, false);

        //goi de set menu tren fragment
        setHasOptionsMenu(true);

        gridView = (GridView) view.findViewById(R.id.image_fragment_gv_images);

        //Giam kich thuoc anh de hien thi
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

    //tao menu
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.mains_action, menu);
    }

    //set su kien click sap xep va may anh
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)    {
        switch (item.getItemId()){
            case R.id.mains_action_sort:
                setReorder();
                return true;
            case R.id.mains_action_camera:
                setCamera();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //set su kien sap xep anh
    private void setReorder() {
        dialog = new Dialog(getContext());
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
                        Collections.sort(imageList, new MapComparator(name, order));
                        break;
                    case R.id.dialog_sort_rb_time:
                        if (order.toLowerCase().contentEquals("asc")) {
                            Collections.sort(imageList, new Comparator<HashMap<String, String>>() {
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
                            Collections.sort(imageList, new Comparator<HashMap<String, String>>() {
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
                            Collections.sort(imageList, new Comparator<HashMap<String, String>>() {
                                @Override
                                public int compare(HashMap<String, String> o1, HashMap<String, String> o2) {
                                    int a = Integer.valueOf(o1.get(Functions.KEY_IMGSIZE));
                                    int b = Integer.valueOf(o2.get(Functions.KEY_IMGSIZE));

                                    return a > b ? 1 : -1;
                                }
                            });
                        }
                        else{
                            Collections.sort(imageList, new Comparator<HashMap<String, String>>() {
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
        window.setLayout(ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        dialog.show();
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
    //het may anh


    //load tat ca anh
    class LoadAll extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listFiles.clear();
            imageList.clear();
        }

        @Override
        protected String doInBackground(String... strings) {
            listFiles.clear();
            imageList.clear();

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
                listFiles.add(Functions.mappingInBox(album, path, timestamp, countPhoto, null));
            }
            cursor.close();
            for (int i = 0; i < listFiles.size(); i++){
                Cursor cursorEx = getContext().getContentResolver().query(uriExternal,
                        projection, "bucket_display_name = \"" + listFiles.get(i).get(Functions.KEY_ALBUM) + "\"",
                        null, null);
                Cursor cursorIn = getContext().getContentResolver().query(uriInternal,
                        projection, "bucket_display_name = \"" + listFiles.get(i).get(Functions.KEY_ALBUM) + "\"",
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
                            imageList.add(Functions.mappingInBox(album, path, timestamp, null, imgSize));
                        }
                    }
                    else {
                        continue;
                    }
                }
                cursor1.close();
            }
            Collections.sort(imageList, new Comparator<HashMap<String, String>>() {
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
            adapter = new ImageAdapter(getActivity(), imageList);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (!pickintent){
                        Intent intent = new Intent(getContext(), FullImage.class);
                        String imgPath = imageList.get(+position).get(Functions.KEY_PATH);
                        intent.putExtra("title", imgPath);
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("list", imageList);
                        intent.putExtras(bundle);
                        intent.putExtra("position", position);
                        startActivity(intent);
                    }
                    else {
                        String imgPath = imageList.get(+position).get(Functions.KEY_PATH);
                        Uri imgUri = Uri.fromFile(new File(imgPath));
                        Intent intentResult = new Intent();
                        intentResult.setData(imgUri);
                        getActivity().setResult(Activity.RESULT_OK, intentResult);
                        getActivity().finish();
                    }
                }
            });
        }
    }

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

        String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE};
        if(!Functions.hasPermissions(getContext(), PERMISSIONS)){
            ActivityCompat.requestPermissions(getActivity(),
                    PERMISSIONS, REQUEST_PERMISSION_KEY);
        }
        else {
            loadAll = new LoadAll();
            loadAll.execute();
        }
    }
}
