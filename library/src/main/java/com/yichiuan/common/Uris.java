package com.yichiuan.common;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

import java.io.File;

/**
 * Uri utility class
 */
public class Uris {
    /**
     * Get {@link File} from Content {@link Uri} or File {@link Uri}.
     *
     * @param context A {@link Context} for the current component.
     * @param uri A {@link Uri} pointing to the file path
     * @return A {@link File} for the Uri, or null if the Uri is invalid or the type is unknown
     */
    @SuppressLint("NewApi")
    public static File getFileFromUri(final Context context, @NonNull final Uri uri) {

        String path = null;

        // Document Uri is available after KITKAT (Android 4.4 API 19)
        final boolean documentUriAvailable = Build.VERSION.SDK_INT >= 19;

        if (documentUriAvailable && DocumentsContract.isDocumentUri(context, uri)) {

            final String authority = uri.getAuthority();

            if ("com.android.providers.media.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];

                Uri mediaUri = null;

                if ("image".equals(type)) {
                    mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    mediaUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                if (mediaUri != null) {
                    final String selection = "_id=" + divide[1];
                    path = queryAbsolutePath(context, mediaUri, selection);
                }
            } else if ("com.android.externalstorage.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] divide = docId.split(":");
                final String type = divide[0];

                if ("primary".equals(type)) {
                    path = Environment.getExternalStorageDirectory().getAbsolutePath()
                            + "/" + divide[1];
                } else {
                    path = "/storage/" + type + "/" + divide[1];
                }

            } else if ("com.android.providers.downloads.documents".equals(authority)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final Uri downloadUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(docId));
                path = queryAbsolutePath(context, downloadUri, null);
            }
        } else {
            final String scheme = uri.getScheme();

            if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
                path = queryAbsolutePath(context, uri, null);
            } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
                path = uri.getPath();
            }
        }
        return (path != null) ? new File(path) : null;
    }

    public static String queryAbsolutePath(final Context context, final Uri uri, String selection) {
        final String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                return cursor.getString(index);
            }
        } catch (final Exception ex) {
            ex.printStackTrace();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }
}
