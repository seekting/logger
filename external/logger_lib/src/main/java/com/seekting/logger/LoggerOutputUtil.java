package com.seekting.logger;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.seekting.logger.async.LoggerThread;
import com.seekting.logger.bean.LoggerMessage;
import com.seekting.logger.callback.LogEventWrapper;
import com.seekting.logger.callback.LoggerEvent;
import com.seekting.logger.io.LogWriter;
import com.seekting.logger.io.LogWriterWrapper;
import com.seekting.logger.io.RandomAccessWriter;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;


public class LoggerOutputUtil {

    private static final AtomicBoolean mIsRunning = new AtomicBoolean(false);
    private static final LogEventWrapper mLogEventWrapper = new LogEventWrapper();
    private static final Handler mHandler = new Handler(Looper.myLooper());

    private static LoggerThread<LoggerMessage> mThread;
    private static String mDir = "";
    public static String mPid;
    public static String mProcessName;
    private static LogWriterWrapper mLogWriterWrapper;

    public static void init(LoggerOutConfig config) {
        if (mIsRunning.compareAndSet(false, true)) {
            mLogEventWrapper.attach(config.loggerEvent);
            mPid = TextUtils.isEmpty(config.pid) ? LoggerEnv.DEFAULT_PID : config.pid;
            mProcessName = TextUtils.isEmpty(config.processName) ? LoggerEnv.DEFAULT_PROCESS_NAME : config.processName;
            if (TextUtils.isEmpty(config.dir)) {
                String externalStorageDirectory = Environment.getExternalStorageDirectory().getPath();
                mDir = externalStorageDirectory + LoggerEnv.LOGGER_OUTPUT_PATH + mProcessName;
            } else {
                mDir = config.dir;
            }
            mLogWriterWrapper = new LogWriterWrapper();
            LogWriter logWriterAdapter = config.logWriter == null ? new RandomAccessWriter() : config.logWriter;
            mLogWriterWrapper.attach(logWriterAdapter, mLogEventWrapper);
            LoggerThread.Builder builder = new LoggerThread.Builder();
            builder.dir(mDir)
                    .loggerEvent(mLogEventWrapper)
                    .logWriter(mLogWriterWrapper)
                    .maxSize(config.maxFileSize == 0 ? LoggerEnv.DEFAULT_MAX_FILE_SIZE : config.maxFileSize)
                    .maxTime(config.maxFileTime == 0 ? LoggerEnv.DEFAULT_MAX_FILE_TIME : config.maxFileTime);
            mThread = builder.build();
            mThread.start();
        } else {
            mLogEventWrapper.dividerException(new RuntimeException("LoggerOutputUtil has init!"));
        }
    }


    public static final class LoggerOutConfig {
        private String dir;
        private String pid;
        private String processName;
        private LoggerEvent loggerEvent;
        private LogWriter logWriter;
        private int maxFileSize;
        private long maxFileTime;

        public LoggerOutConfig() {
        }

        public LoggerOutConfig dir(String val) {
            dir = val;
            return this;
        }

        public LoggerOutConfig pid(String val) {
            pid = val;
            return this;
        }

        public LoggerOutConfig processName(String val) {
            processName = val;
            return this;
        }

        public LoggerOutConfig loggerEvent(LoggerEvent val) {
            loggerEvent = val;
            return this;
        }

        public LoggerOutConfig logWriter(LogWriter val) {
            logWriter = val;
            return this;
        }

        public LoggerOutConfig fileMaxByteSize(int val) {
            if (val < LoggerEnv.MIN_FILE_SIZE) {
                throw new IllegalArgumentException("fileMaxByteSize must >=" + LoggerEnv.MIN_FILE_SIZE);
            }
            maxFileSize = val;
            return this;
        }

        public LoggerOutConfig fileOverDueTimeMillis(long val) {
            if (val < LoggerEnv.MIN_FILE_TIME) {
                throw new IllegalArgumentException("fileOverDueTimeMillis must >=" + LoggerEnv.MIN_FILE_TIME);
            }
            maxFileTime = val;
            return this;
        }
    }

    public static void dumpLogcat(String reason) {
        String date = DateUtil.format();
        File file = new File(mDir);
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
        String realFileName = "Logcat_" + reason + "_" + date + ".txt";
        cmdLine.add(realFileName);
        mLogEventWrapper.onRecordLogcat("FileName=" + file + realFileName);
        try {

            final Process process = Runtime.getRuntime().exec((String[]) cmdLine.toArray(new String[cmdLine.size()]), (String[]) null, file);
            mHandler.postDelayed(new Runnable() {
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
                .mPid(mPid)
                .mTid(android.os.Process.myTid())
                .mProcessName(mProcessName);
        LoggerMessage loggerMessage = builder.build();
        mThread.put(loggerMessage);

    }


}

