package com.yichiuan.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.yichiuan.common.Strings;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isEmpty = Strings.isNullOrEmpty("good");
        Log.i(TAG, "isEmpty: " + isEmpty);
    }
}
