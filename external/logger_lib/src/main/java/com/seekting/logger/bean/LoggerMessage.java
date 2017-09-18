package com.seekting.logger.bean;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2017/8/31.
 */

public class LoggerMessage implements LogcatAble {
    private static final String pattern = "MM-dd HH:mm:ss.SSS";
    private static final Date date = new Date();
    private static final SimpleDateFormat simpleFormatter = new SimpleDateFormat(pattern);


    private final int mTid;
    private final int mLogType;
    private final long mTime;
    private final String mTag;
    private final Object mMsg;
    private final Throwable mThrowable;
    private final String mPid;
    private final String mProcessName;

    private LoggerMessage(Builder builder) {
        mTid = builder.mTid;
        mLogType = builder.mLogType;
        mTime = builder.mTime;
        mTag = builder.mTag;
        mMsg = builder.mMsg;
        mThrowable = builder.mThrowable;
        mPid = builder.mPid;
        mProcessName = builder.mProcessName;
    }

    @Override
    public String getTag() {
        return mTag;
    }

    @Override
    public Object getMsg() {
        return mMsg;
    }

    @Override
    public String getLine() {
        return toString();
    }

    @Override
    public String toString() {
        date.setTime(mTime);
        String timeStr = simpleFormatter.format(date);

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(timeStr);
        stringBuilder.append(" ");
        stringBuilder.append(mPid);
        stringBuilder.append("-");
        stringBuilder.append(mTid);
        stringBuilder.append("/");
        stringBuilder.append(mProcessName);
        stringBuilder.append(" ");
        stringBuilder.append(LOG_TYPE_NAMES.get(mLogType));
        stringBuilder.append("/");
        stringBuilder.append(mTag);
        stringBuilder.append(": ");
        stringBuilder.append(mMsg);
        stringBuilder.append("\n");
        if (mThrowable != null) {
            stringBuilder.append(getStackTraceString(mThrowable));

        }
        return stringBuilder.toString();


    }


    public static String getStackTraceString(Throwable tr) {
        if (tr == null) {
            return "";
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        Throwable t = tr;
        while (t != null) {
            if (t instanceof UnknownHostException) {
                return "";
            }
            t = t.getCause();
        }

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        tr.printStackTrace(pw);
        pw.flush();
        return sw.toString();
    }

    /**
     * Priority constant for the println method; use Log.v.
     */
    public static final int VERBOSE = 2;

    /**
     * Priority constant for the println method; use Log.d.
     */
    public static final int DEBUG = 3;

    /**
     * Priority constant for the println method; use Log.i.
     */
    public static final int INFO = 4;

    /**
     * Priority constant for the println method; use Log.w.
     */
    public static final int WARN = 5;

    /**
     * Priority constant for the println method; use Log.e.
     */
    public static final int ERROR = 6;

    /**
     * Priority constant for the println method.
     */
    public static final int ASSERT = 7;

    private static final Map<Integer, String> LOG_TYPE_NAMES = new HashMap<>();

    static {
        LOG_TYPE_NAMES.put(VERBOSE, "V");
        LOG_TYPE_NAMES.put(DEBUG, "D");
        LOG_TYPE_NAMES.put(INFO, "I");
        LOG_TYPE_NAMES.put(WARN, "W");
        LOG_TYPE_NAMES.put(ERROR, "E");
        LOG_TYPE_NAMES.put(ASSERT, "A");

    }


    public static final class Builder {
        private int mTid;
        private int mLogType;
        private long mTime;
        private String mTag;
        private Object mMsg;
        private Throwable mThrowable;
        private String mPid;
        private String mProcessName;

        public Builder() {
        }

        public Builder mTid(int val) {
            mTid = val;
            return this;
        }

        public Builder mLogType(int val) {
            mLogType = val;
            return this;
        }

        public Builder mTime(long val) {
            mTime = val;
            return this;
        }

        public Builder mTag(String val) {
            mTag = val;
            return this;
        }

        public Builder mMsg(Object val) {
            mMsg = val;
            return this;
        }

        public Builder mThrowable(Throwable val) {
            mThrowable = val;
            return this;
        }

        public Builder mPid(String val) {
            mPid = val;
            return this;
        }

        public Builder mProcessName(String val) {
            mProcessName = val;
            return this;
        }

        public LoggerMessage build() {
            return new LoggerMessage(this);
        }
    }
}
