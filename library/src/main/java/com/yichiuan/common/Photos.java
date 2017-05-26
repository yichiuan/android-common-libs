package com.yichiuan.common;

import android.support.media.ExifInterface;

import java.io.IOException;
import java.io.InputStream;

public class Photos {
    public static int getOrientationFromExif(String filename) throws IOException {

        ExifInterface exif = new ExifInterface(filename);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        return getDegree(orientation);
    }

    public static int getOrientationFromExif(InputStream is) throws IOException {

        ExifInterface exif = new ExifInterface(is);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        return getDegree(orientation);
    }

    public static int getDegree(int orientation) {
        int degree;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            default:
                degree = 0;
        }

        return degree;
    }
}
