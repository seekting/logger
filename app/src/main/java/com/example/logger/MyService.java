package com.example.logger;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.seekting.logger.LoggerOutputUtil;

import static com.example.logger.MainActivity.INT_TIME;
import static com.example.logger.MainActivity.INT_TIMES;
import static com.example.logger.MainActivity.sss;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < INT_TIMES; i++) {
                    LoggerOutputUtil.d(TAG, "i=" + i + sss);
                    SystemClock.sleep((long) (Math.random() * INT_TIME));
                }

                Log.d("seekting", "MyService.run() over");
            }
        }).start();
    }

    public static void startService(Context c) {
        c.startService(new Intent(c, MyService.class));

    }
}
