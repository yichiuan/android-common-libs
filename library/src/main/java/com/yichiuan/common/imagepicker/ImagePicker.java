package com.yichiuan.common.imagepicker;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class ImagePicker {

    private static final int REQUEST_IDENTIFICATOR = 0b1011001010;
    private static final int REQUEST_TAKE_PHOTO = REQUEST_IDENTIFICATOR + (1 << 11);

    private static final String FILEPROVIDER_AUTHORITY
            = "com.yichiuan.common.imagepicker.fileprovider";

    private static final String DEFAULT_FOLDER_NAME = "ImagePicker";

    private static File currentImageFile = null;

    public interface Callbacks {
        void onImagePickerError(Exception e);

        void onImagesPicked(@NonNull List<File> imageFiles);

        void onCanceled();
    }

    /**
     * Request a image from camera app
     *
     * @param activity a activity reference
     */
    @MainThread
    public static void openCamera(@NonNull Activity activity) {

        final Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(activity.getPackageManager()) != null) {

            // Create the File where the photo should go
            currentImageFile = createImageFile(activity);

            if (currentImageFile != null) {
                Uri photoUri = FileProvider.getUriForFile(activity, FILEPROVIDER_AUTHORITY,
                        currentImageFile);
                grantUriPermission(activity, takePictureIntent, photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                activity.startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    private static void grantUriPermission(Context context, Intent intent, Uri photoUri,
            int permissionFlags) {
        // Workaround for Uri Permissions for EXTRA_OUTPUT on ACTION_IMAGE_CAPTURE on API < 21
        // see https://commonsware.com/blog/2016/08/31/granting-permissions-uri-intent-extra.html
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            ClipData clip =
                    ClipData.newUri(context.getContentResolver(), "A photo", photoUri);

            intent.setClipData(clip);
            intent.addFlags(permissionFlags);
        }
    }

    @MainThread
    public static void handleActivityResult(int requestCode, int resultCode,
            @NonNull Callbacks callbacks) {

        boolean isImagePickerRequest = (requestCode & REQUEST_IDENTIFICATOR) > 0;

        if (!isImagePickerRequest) return;

        if (resultCode != Activity.RESULT_OK) {
            callbacks.onCanceled();
        } else {
            switch (requestCode) {
                case REQUEST_TAKE_PHOTO:
                    if (currentImageFile != null) {
                        ArrayList<File> files = new ArrayList<>();
                        files.add(currentImageFile);
                        callbacks.onImagesPicked(files);
                    } else {
                        Exception e = new IllegalStateException(
                                "Unable to get the picture returned from camera");
                        callbacks.onImagePickerError(e);
                    }
                    break;
            }
        }

        cleanState();
    }

    private static File createImageFile(@NonNull Context context) {
        File storageDir = new File(context.getCacheDir(), DEFAULT_FOLDER_NAME);
        if (!storageDir.exists()) storageDir.mkdir();
        return new File(storageDir, UUID.randomUUID().toString() + ".jpg");
    }

    private static void cleanState() {
        currentImageFile = null;
    }
}
