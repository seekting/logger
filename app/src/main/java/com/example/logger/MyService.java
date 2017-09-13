package com.example.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.seekting.logger.LoggerOutputUtil;

/**
 * Created by Administrator on 2017/9/13.
 */

public class MyService extends Service {
    public static final String TAG = "MyService";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LoggerOutputUtil.d(TAG, "100");
        LoggerOutputUtil.d(TAG, "101", new NullPointerException());
    }

    public static void startService(Context c) {
        c.startService(new Intent(c, MyService.class));

    }
}
