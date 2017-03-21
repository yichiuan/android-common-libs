package com.yichiuan.common.imagepickersample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.yichiuan.common.Bitmaps;
import com.yichiuan.common.Photos;
import com.yichiuan.common.imagepicker.ImagePicker;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    Button takePictureButton;

    ImageView showImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showImageView = (ImageView) findViewById(R.id.imageview_show);

        takePictureButton = (Button) findViewById(R.id.button_take_picture);

        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.openCamera(MainActivity.this);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i(TAG, "onActivityResult() ResultCode: " + resultCode);

        ImagePicker.handleActivityResult(requestCode, resultCode, new ImagePicker.Callbacks() {
            @Override
            public void onImagePickerError(Exception e) {
                Log.e(TAG, e.getMessage());
            }

            @Override
            public void onImagesPicked(@NonNull List<File> imageFiles) {
                int requestWidth = showImageView.getWidth();
                int requestHeight = showImageView.getHeight();

                String filePath = imageFiles.get(0).getAbsolutePath();

                Bitmap bitmap = Bitmaps.decodeSampledBitmapFromFile(filePath, requestWidth,
                        requestHeight);

                try {
                    int degree = Photos.getOrientationFromExif(filePath);
                    showImageView.setImageBitmap(Bitmaps.rotateBitmap(bitmap, degree));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
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
