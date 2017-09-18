package com.seekting.logger.async;

import android.text.TextUtils;

import com.seekting.logger.LoggerEnv;
import com.seekting.logger.bean.LogcatAble;
import com.seekting.logger.callback.LoggerEvent;
import com.seekting.logger.io.LogWriterWrapper;

import java.io.File;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

import static com.seekting.logger.LoggerEnv.SIMPLE_DATE_FORMAT;


/**
 * Created by Administrator on 2017/8/31.
 */

public class LoggerThread<T extends LogcatAble> extends Thread {
    public static final String EXTENSION_NAME = ".txt";
    public static final String TOO_OLD_REASON = "this file is too old ,create a new file";
    public static final String TOO_LARGE_REASON = "this file is too large ,create a new file";
    public static final String NEW_DIR = "#no dir and new a log file\n";
    public static final String NEW_FILE = "#can't find any file,new a log file\n";
    private static String S_NEW_FILE = "# newFile=%s,reason:%s\n";
    private static String S_OLD_FILE = "# oldFile=%s,reason:%s\n";


    public static final Date DATE = new Date();


    private LinkedBlockingQueue<T> mLinkedBlockingQueue;
    private String dir;
    private LogWriterWrapper mLogWriter;
    private LoggerEvent mLoggerEvent;
    private int mMaxSize;
    private long mMaxTime;



    private LoggerThread(Builder builder) {
        dir = builder.dir;
        mLogWriter = builder.mLogWriter;
        mLoggerEvent = builder.mLoggerEvent;
        mMaxSize = builder.mMaxSize;
        mMaxTime = builder.mMaxTime;
        mLinkedBlockingQueue = new LinkedBlockingQueue<>();
    }

    public void put(T t) {
        try {
            mLinkedBlockingQueue.put(t);
        } catch (InterruptedException e) {
            mLoggerEvent.dividerException(e);
        }
    }

    @Override
    public void run() {
        while (true) {
            T t = null;
            try {
                t = mLinkedBlockingQueue.take();
            } catch (InterruptedException e) {
                mLoggerEvent.dividerException(e);
            }

            if (t == null) {
                continue;
            }

            ObtainFile obtainFile = obtainFile();
            if (obtainFile.targetFile != null) {
                if (obtainFile.oldFile != null) {
                    onChangeNewFile(obtainFile.oldFile, obtainFile.targetFile, obtainFile.reason);
                } else {
                    if (obtainFile.reason != null) {
                        mLogWriter.writeText(obtainFile.targetFile, obtainFile.reason);
                    }
                }
                mLogWriter.preWrite(obtainFile.targetFile, t.getTag(), t.getMsg());
                mLogWriter.doWrite(obtainFile.targetFile, t.getTag(), t.getMsg(), t.getLine());
                mLogWriter.onPostWrite(obtainFile.targetFile, t.getTag(), t.getMsg());
            }


        }


    }


    private void onChangeNewFile(File oldFile, File newFile, String reason) {
        mLogWriter.writeText(oldFile, String.format(S_NEW_FILE, newFile.getName(), reason));
        mLogWriter.writeText(newFile, String.format(S_OLD_FILE, oldFile.getName(), reason));
    }

    private ObtainFile obtainFile() {
        ObtainFile obtainFile = new ObtainFile();
        File dirFile = new File(dir);

        if (dirFile.exists()) {
//            File[] files = dirFile.listFiles();
            String[] fileNames = dirFile.list();
            long maxTime = -1;
            String hitFileName = null;
            long current = System.currentTimeMillis();

            //find recently file by name;
            for (int i = 0; i < fileNames.length; i++) {
                String fileName = fileNames[i];
                if (TextUtils.isEmpty(fileName) || fileName.length() < LoggerEnv.pattern.length()) {
                    continue;
                }
                try {
                    Date date = SIMPLE_DATE_FORMAT.parse(fileName.substring(0, LoggerEnv.pattern.length()));
                    long dateTime = date.getTime();
                    //if find a file's timestamp>current,ignore this file
                    if (dateTime > current) {
                        continue;
                    }
                    if (date.getTime() > maxTime) {
                        maxTime = date.getTime();
                        hitFileName = fileName;
                    }

                } catch (Throwable e) {
                    continue;
                }

            }
            if (hitFileName != null) {
                long delta = current - maxTime;
                if (delta > mMaxTime) {
                    //this file is too old ,create a new file"
                    obtainFile.targetFile = createNewFile(dir);
                    obtainFile.oldFile = new File(dir, hitFileName);
                    obtainFile.reason = TOO_OLD_REASON;
                } else {
                    File f = new File(dir, hitFileName);
                    long length = f.length();
                    // this file is too large ,create a new file
                    if (length > mMaxSize) {
                        obtainFile.targetFile = createNewFile(dir);
                        obtainFile.oldFile = new File(dir, hitFileName);
                        obtainFile.reason = TOO_LARGE_REASON;
                    } else {
                        obtainFile.targetFile = new File(dir, hitFileName);
                    }
                }
            } else {
                //can't find any file,new a log file
                obtainFile.targetFile = createNewFile(dir);
                obtainFile.reason = NEW_FILE;
            }


        } else {
            //no dir and new a log file
            dirFile.mkdirs();
            obtainFile.targetFile = createNewFile(dir);
            obtainFile.reason = NEW_DIR;
        }
        return obtainFile;
    }

    private File createNewFile(String dir) {
        DATE.setTime(System.currentTimeMillis());
        String fileName = SIMPLE_DATE_FORMAT.format(DATE) + EXTENSION_NAME;
        File result = new File(dir, fileName);
        return result;

    }

    static class ObtainFile {
        File targetFile;
        File oldFile;
        String reason;

    }


    public static final class Builder {
        private String dir;
        private LogWriterWrapper mLogWriter;
        private LoggerEvent mLoggerEvent;
        private int mMaxSize;
        private long mMaxTime;

        public Builder() {
        }

        public Builder dir(String val) {
            dir = val;
            return this;
        }

        public Builder logWriter(LogWriterWrapper val) {
            mLogWriter = val;
            return this;
        }

        public Builder loggerEvent(LoggerEvent val) {
            mLoggerEvent = val;
            return this;
        }

        public Builder maxSize(int val) {
            mMaxSize = val;
            return this;
        }

        public Builder maxTime(long val) {
            mMaxTime = val;
            return this;
        }

        public LoggerThread build() {
            return new LoggerThread(this);
        }
    }
}
