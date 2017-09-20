package com.example.logger;

import android.app.Application;
import android.os.Environment;
import android.util.Log;

import com.seekting.logger.LoggerOutputUtil;
import com.seekting.logger.callback.LoggerEvent;

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

        LoggerEvent loggerEvent = new LoggerEvent() {
            @Override
            public void onPreWrite(File f, String tag, Object msg) {
//                Log.d("seekting", "App.onPreWrite()" + f + msg);


            }

            @Override
            public void onWrite(File f, String tag, Object msg) {
                Log.d("seekting", "App.onPreWrite()" + f + msg);
            }

            @Override
            public void onPostWrite(File f, String tag, Object msg) {

            }

            @Override
            public void dividerException(File f, Throwable t) {

                Log.d("seekting", "App.onException()", t);

            }

            @Override
            public void dividerException(Throwable t) {

                Log.d("seekting", "App.onException()", t);

            }


            @Override
            public void onWriteText(File file, String text) {

            }

            @Override
            public void onRecordLogcat(String realFileName) {

                Log.d("seekting", "App.onRecordLogcat()" + realFileName);
            }


        };
        LoggerOutputUtil.LoggerOutConfig config = new LoggerOutputUtil.LoggerOutConfig();
        config.dir(myDir.getAbsolutePath())
                .pid(pid + "")
                .processName(processName)
                .loggerEvent(loggerEvent)
                .fileMaxByteSize(4 * 1024 * 1024)
                .fileOverDueTimeMillis(5 * 60 * 1000);
//                .logWriter(new LogWriter() {
//                    @Override
//                    public void doWrite(File f, String tag, Object msg, String line) {
//                        Log.d("seekting", "App.doWrite()" + line);
//                    }
//                });

        LoggerOutputUtil.init(config);


    }

    public static void clear() {
        File dir1 = Environment.getExternalStorageDirectory();
        File myDir = new File(dir1, "logger_demo");
        if (myDir.isDirectory()) {
            File[] l = myDir.listFiles();
            for (File file : l) {
                if (!file.isDirectory()) {
                    boolean suc = file.delete();
                    Log.d("seekting", "App.clear()" + file + ",suc=" + suc);
                }
            }

        } else {
            boolean suc = myDir.delete();
            Log.d("seekting", "App.clear()" + myDir + ",suc=" + suc);
        }
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
