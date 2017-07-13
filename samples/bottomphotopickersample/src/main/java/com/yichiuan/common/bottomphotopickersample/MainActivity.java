package com.yichiuan.common.bottomphotopickersample;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.yichiuan.common.bottomphotopicker.MediaPicker;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    RecyclerView photoPickerView;

    MediaPicker mediaPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoPickerView = findViewById(R.id.recyclerView);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            mediaPicker =MediaPicker.with(photoPickerView);
            loadMediaData();
        }
    }

    private void loadMediaData() {
        getSupportLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMediaData();
                } else {
                    new AlertDialog.Builder(this)
                            .setMessage("Need Read External Storage.")
                            .create()
                            .show();
                }
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        final String[] columns = {MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA};

        return new CursorLoader(this,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                columns, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mediaPicker.load(cursor)
                .subscribe(uris -> {
                    for (Uri uri : uris) {
                        Log.i("MainActivity", "Uri: " + uri);
                    }
                });
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
