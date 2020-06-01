package com.example.galleryimgapp.FullImage;

import android.media.ExifInterface;

import java.io.File;

public class ImageDetails {
    String title, time, width, height, imgsize, path;
    ExifInterface exif;

    public ImageDetails(String path, ExifInterface exif) {
        this.path = path;
        this.exif = exif;
    }

    public String getTitle() {
        title = path.substring(path.lastIndexOf("/") + 1);
        return title;
    }

    public String getTime() {
        time = exif.getAttribute(ExifInterface.TAG_DATETIME);
        return time;
    }

    public String getWidth() {
        width = exif.getAttribute(ExifInterface.TAG_IMAGE_WIDTH);
        return width;
    }

    public String getHeight() {
        height = exif.getAttribute(ExifInterface.TAG_IMAGE_LENGTH);
        return height;
    }

    public String getImgsize() {
        File file = new File(path);
        imgsize = String.valueOf((int) (file.length() / 1024));
        return imgsize;
    }

    public String getPath() {
        return path;
    }
}
