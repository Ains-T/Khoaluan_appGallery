package com.example.galleryimgapp.Utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MergeCursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import androidx.core.app.ActivityCompat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Functions {

    static final public String KEY_ALBUM = "album_name";
    static final public String KEY_PATH = "path";
    static final public String KEY_TIMESTAMP = "timestamp";
    static final public String KEY_TIME = "date";
    static final public String KEY_COUNT = "count";
    static final public String KEY_IMGSIZE = "size";

    public static boolean hasPermissions(Context context, String... permissions){
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M
        && context != null && permissions != null){
            for (String permission : permissions){
                if (ActivityCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED){
                    return false;
                }
            }
        }
        return true;
    }

    public static HashMap<String, String> mappingInBox(String album, String path, String timestamp, String count, String imgsize){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(KEY_ALBUM, album);
        map.put(KEY_PATH, path);
        if (timestamp == null){
            timestamp = "";
        }
        map.put(KEY_TIMESTAMP, timestamp);
        map.put(KEY_COUNT, count);
        map.put(KEY_IMGSIZE, imgsize);
        return map;
    }

    public static String getCount(Context context, String album_name){
        Uri uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        Uri uriInternal = MediaStore.Images.Media.INTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED};
        Cursor cursorExternal = context.getContentResolver().query(uriExternal, projection,
                "bucket_display_name = \"" + album_name + "\"", null, null);
        Cursor cursorInternal = context.getContentResolver().query(uriInternal, projection,
                "bucket_display_name = \"" + album_name + "\"", null, null);
        Cursor cursor = new MergeCursor(new Cursor[]{cursorExternal, cursorInternal});

        return cursor.getCount() + " Photos";
    }

    public static String converToTime(String timestamp){
        if (timestamp == null){
            return "";
        }
        long datetime = Long.parseLong(timestamp);
        Date date = new Date(datetime);
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return formatter.format(date);
    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
