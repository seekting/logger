package com.example.logger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.seekting.logger.LoggerOutputUtil;

public class MainActivity extends AppCompatActivity {


    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LoggerOutputUtil.d(TAG, "100");
        LoggerOutputUtil.d(TAG, "101", new NullPointerException());
        MyService.startService(this);


    }


}
