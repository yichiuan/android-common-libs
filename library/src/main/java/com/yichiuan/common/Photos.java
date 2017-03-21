package com.yichiuan.common;

import android.media.ExifInterface;

import java.io.IOException;

public class Photos {
    public static int getOrientationFromExif(String filename) throws IOException {

        ExifInterface exif = new ExifInterface(filename);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        int degree;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                degree = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                degree = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                degree = 90;
                break;
            default:
                degree = 0;
        }

        return degree;
    }
}
