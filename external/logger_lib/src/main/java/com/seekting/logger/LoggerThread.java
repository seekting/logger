package com.seekting.logger;

import android.text.TextUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Administrator on 2017/8/31.
 */

public class LoggerThread<T> extends Thread {
    public static final String EXTENSION_NAME = ".txt";
    public static final String TOO_OLD_REASON = "this file is too old ,create a new file";
    public static final String TOO_LARGE_REASON = "this file is too large ,create a new file";
    public static final String NEW_DIR = "#no dir and new a log file\n";
    public static final String NEW_FILE = "#can't find any file,new a log file\n";
    private static String S_NEW_FILE = "# newFile=%s,reason:%s\n";
    private static String S_OLD_FILE = "# oldFile=%s,reason:%s\n";
    private LinkedBlockingQueue<T> mLinkedBlockingQueue;
    private String dir;
    //every log max file length 10M
    public static final int MAX_SIZE = 10 * 1024 * 1024;/*byte*/
    //    public static final int MAX_SIZE = 8 * 10 * 10;/*byte*/
    //every log record recently 30minutes event
    public static final long MAX_TIME = 30 * 60 * 1000;
    public static final String pattern = "yyyy-MM-dd-HH_mm_ss_SSS";
    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat(pattern);
    public static final Date DATE = new Date();
    private ILogWriter<T> mTILogWriter;

    public LoggerThread(String dir, LinkedBlockingQueue<T> linkedBlockingQueue, ILogWriter<T> iLogWriter) {
        this.dir = dir;
        mLinkedBlockingQueue = linkedBlockingQueue;
        mTILogWriter = iLogWriter;
    }

    @Override
    public void run() {
        while (true) {
            T t = null;
            try {
                t = mLinkedBlockingQueue.take();
            } catch (InterruptedException e) {
                if (LoggerOutputUtil.S_loggerEvent != null) {
                    LoggerOutputUtil.S_loggerEvent.onException(null, e);
                }
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
                        mTILogWriter.writeText(obtainFile.targetFile, obtainFile.reason);
                    }
                }
                mTILogWriter.preWrite(obtainFile.targetFile, t);
                mTILogWriter.doWrite(obtainFile.targetFile, t);
                mTILogWriter.onPostWrite(obtainFile.targetFile, t);
            }


        }


    }


    private void onChangeNewFile(File oldFile, File newFile, String reason) {
        mTILogWriter.writeText(oldFile, String.format(S_NEW_FILE, newFile.getName(), reason));
        mTILogWriter.writeText(newFile, String.format(S_OLD_FILE, oldFile.getName(), reason));
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
                if (TextUtils.isEmpty(fileName) || fileName.length() < pattern.length()) {
                    continue;
                }
                try {
                    Date date = SIMPLE_DATE_FORMAT.parse(fileName.substring(0, pattern.length()));
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
                if (delta > MAX_TIME) {
                    //this file is too old ,create a new file"
                    obtainFile.targetFile = createNewFile(dir);
                    obtainFile.oldFile = new File(dir, hitFileName);
                    obtainFile.reason = TOO_OLD_REASON;
                } else {
                    File f = new File(dir, hitFileName);
                    long length = f.length();
                    // this file is too large ,create a new file
                    if (length > MAX_SIZE) {
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


}
