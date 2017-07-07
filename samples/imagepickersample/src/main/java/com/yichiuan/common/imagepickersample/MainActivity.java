package com.yichiuan.common.imagepickersample;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yichiuan.common.Bitmaps;
import com.yichiuan.common.Photos;
import com.yichiuan.common.imagepicker.ImagePicker;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    Button takePictureButton;
    Button pickFromGalleryButton;
    Button pickFromDocumentsButton;

    ImageView showImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showImageView = (ImageView) findViewById(R.id.imageview_show);

        takePictureButton = (Button) findViewById(R.id.button_take_picture);
        takePictureButton.setOnClickListener(v -> ImagePicker.openCamera(this));

        pickFromGalleryButton = (Button) findViewById(R.id.button_pick_from_gallery);
        pickFromGalleryButton.setOnClickListener(v -> ImagePicker.openGallery(this, true));

        pickFromDocumentsButton = (Button) findViewById(R.id.button_pick_from_documents);
        pickFromDocumentsButton.setOnClickListener(v -> ImagePicker.openDocuments(this, true));

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult() ResultCode: " + resultCode);

        ImagePicker.handleActivityResult(requestCode, resultCode, data,
                new ImagePicker.Callbacks() {
                    @Override
                    public void onImagePickerError(Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                    @Override
                    public void onImagesPicked(@NonNull List<Uri> imageFiles) {
                        int requestWidth = showImageView.getWidth();
                        int requestHeight = showImageView.getHeight();

                        Uri uri = imageFiles.get(0);

                        AssetFileDescriptor fd = null;
                        try {
                            fd = getContentResolver().openAssetFileDescriptor(uri, "r");

                            Bitmap bitmap = Bitmaps.decodeSampledBitmapFromFileDescriptor(
                                    fd.getFileDescriptor(), requestWidth, requestHeight);

                            int degree = Photos.getOrientationFromExif(fd.createInputStream());
                            showImageView.setImageBitmap(Bitmaps.rotateBitmap(bitmap, degree));

                        } catch (IOException e) {
                            Log.e(TAG, e.getMessage());
                        } finally {
                            if (fd != null) {
                                try {
                                    fd.close();
                                } catch (IOException ignored) {
                                }
                            }
                        }
                    }

                    @Override
                    public void onCanceled() {
                        Toast.makeText(getApplicationContext(), "ImagePicker onCanceled()",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
