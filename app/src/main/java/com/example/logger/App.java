package com.example.logger;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.seekting.logger.LoggerEvent;
import com.seekting.logger.LoggerOutputUtil;

import java.io.File;
import java.io.FileInputStream;

import static com.android.internal.app.IntentForwarderActivity.TAG;

/**
 * Created by Administrator on 2017/9/13.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        File dir = Environment.getExternalStorageDirectory();
        File myDir = new File(dir, "logger_demo");

        int pid = android.os.Process.myPid();
        String processName = getCurrentProcessName();
        Log.d("seekting", "App.onCreate()processName=" + processName);
        LoggerEvent loggerEvent = new LoggerEvent() {
            @Override
            public void onPreWrite(File f, String tag, String msg) {
                Log.d("seekting", "App.onPreWrite()" + f + msg);

            }

            @Override
            public void onWrite(File f, String tag, String msg) {

            }

            @Override
            public void onPostWrite(File f, String tag, String msg) {

            }

            @Override
            public void onException(File f, Throwable t) {

            }

            @Override
            public int getTid() {
                return 0;
            }

            @Override
            public void onWriteText(File file, String text) {

            }

            @Override
            public void onRecordLogcat(String realFileName) {

            }
        };
        LoggerOutputUtil.init(myDir.getAbsolutePath(), pid + "", processName, loggerEvent);


    }


    /**
     * 返回当前的进程名
     */
    public static String getCurrentProcessName() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream("/proc/self/cmdline");

            byte[] buffer = new byte[32];

            int len = 0;
            int b;
            while ((b = fis.read()) > 0 && len < buffer.length) {
                buffer[len++] = (byte) b;
            }
            if (len > 0) {
                String s = new String(buffer, 0, len, "UTF-8");
                return s;
            }
        } catch (Exception e) {
            Log.e(TAG, "", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }

        return null;
    }
}
