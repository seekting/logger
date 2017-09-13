package com.seekting.logger;

import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.seekting.logger.LoggerThread.SIMPLE_DATE_FORMAT;


public class LoggerOutputUtil {

    public static String s_dir = "";
    public static LinkedBlockingQueue<LoggerMessage> mLinkedBlockingQueue = new LinkedBlockingQueue<>();
    public static LoggerThread<LoggerMessage> mThread;
    public static AtomicBoolean isRunning = new AtomicBoolean(false);
    public static String S_Pid;
    public static String S_processName;
    static LoggerEvent S_loggerEvent;
    static Handler sHandler = new Handler(Looper.myLooper());

    public static void init(String dir, String pid, String processName, LoggerEvent loggerEvent) {

        if (isRunning.compareAndSet(false, true)) {
            S_loggerEvent = loggerEvent;
            s_dir = dir;
            S_Pid = pid;
            S_processName = processName;
            ILogWriter<LoggerMessage> iLogWriter = new RandomAccessWriter<>(loggerEvent);
            mThread = new LoggerThread(s_dir, mLinkedBlockingQueue, iLogWriter);
            mThread.start();
        } else {
            throw new RuntimeException("LoggerOutputUtil has init!");
        }
    }

    public static void clear(File dir) {
        if (dir.isDirectory()) {
            File[] l = dir.listFiles();
            for (File file : l) {
                if (!file.isDirectory()) {
                    file.delete();
                    S_loggerEvent.onClear("delete:" + file.getAbsolutePath());
                }
            }

        } else {
            dir.delete();
            S_loggerEvent.onClear("delete:" + dir.getAbsolutePath());
        }
    }

    public static void recordLogcat(String fileTag) {
        String date = SIMPLE_DATE_FORMAT.format(new Date());
        File file = new File(s_dir);
        if (!file.exists()) {
            boolean suc = file.mkdirs();
            if (!suc) {
                return;
            }
        }

        ArrayList cmdLine = new ArrayList();
        cmdLine.add("logcat");
        cmdLine.add("-d");
        cmdLine.add("-v");
        cmdLine.add("threadtime");
        cmdLine.add("-f");
        String realFileName = "Logcat_" + fileTag + "_" + date + ".txt";
        cmdLine.add(realFileName);
        if (S_loggerEvent != null) {
            S_loggerEvent.onRecordLogcat("FileName=" + file + realFileName);
        }
        try {

            final Process process = Runtime.getRuntime().exec((String[]) cmdLine.toArray(new String[cmdLine.size()]), (String[]) null, file);
            sHandler.postDelayed(new Runnable() {
                public void run() {
                    process.destroy();
                }
            }, 1000L);
        } catch (Exception var4) {
            ;
        }

    }

    public static void v(String tag, Object msg) {
        log(LoggerMessage.VERBOSE, tag, msg);
    }

    public static void v(String tag, Object msg, Throwable t) {
        log(LoggerMessage.VERBOSE, tag, msg, t);
    }

    public static void d(String tag, Object msg) {
        log(LoggerMessage.DEBUG, tag, msg);
    }

    public static void d(String tag, Object msg, Throwable t) {
        log(LoggerMessage.DEBUG, tag, msg, t);
    }

    public static void i(String tag, Object msg) {
        log(LoggerMessage.INFO, tag, msg);
    }

    public static void i(String tag, Object msg, Throwable t) {
        log(LoggerMessage.INFO, tag, msg, t);
    }

    public static void w(String tag, Object msg) {
        log(LoggerMessage.WARN, tag, msg);
    }

    public static void w(String tag, Object msg, Throwable t) {
        log(LoggerMessage.WARN, tag, msg, t);
    }

    public static void e(String tag, Object msg) {
        log(LoggerMessage.ERROR, tag, msg);
    }

    public static void e(String tag, Object msg, Throwable t) {
        log(LoggerMessage.ERROR, tag, msg, t);
    }

    public static void a(String tag, Object msg) {
        log(LoggerMessage.ASSERT, tag, msg);
    }

    public static void a(String tag, Object msg, Throwable t) {
        log(LoggerMessage.ASSERT, tag, msg, t);
    }

    private static void log(int logType, String tag, Object msg) {
        log(logType, tag, msg, null);
    }

    private static void log(int logType, String tag, Object msg, Throwable t) {
        log(logType, System.currentTimeMillis(), tag, msg, t);
    }

    private static void log(int logType, long time, String tag, Object msg, Throwable t) {
        LoggerMessage.Builder builder = new LoggerMessage.Builder();
        builder.mLogType(logType)
                .mTime(time)
                .mTag(tag)
                .mMsg(msg)
                .mThrowable(t)
                .mPid(S_Pid)
                .mTid(android.os.Process.myTid())
                .mProcessName(S_processName);
        LoggerMessage loggerMessage = builder.build();
        try {
            mLinkedBlockingQueue.put(loggerMessage);
        } catch (InterruptedException e) {
            if (S_loggerEvent != null) {
                S_loggerEvent.onException(null, e);
            }
        }

    }


}

