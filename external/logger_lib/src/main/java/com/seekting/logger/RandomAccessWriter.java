package com.seekting.logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

/**
 * Created by Administrator on 2017/8/31.
 */

public class RandomAccessWriter<T> extends BaseLogWriter<T> {


    public RandomAccessWriter(LoggerEvent loggerEvent) {
        super(loggerEvent);
    }

    @Override
    public void preWrite(File f, T t) {

        if (mLoggerEvent != null) {
            if (t instanceof LoggerMessage) {
                LoggerMessage loggerMessage = (LoggerMessage) t;
                mLoggerEvent.onPreWrite(f, loggerMessage.mTag, loggerMessage.mMsg.toString());
            }
        }
    }

    @Override
    public void doWrite(File file, T t) {
        String line = t.toString();
        byte[] bytes = line.getBytes();
        write(file, bytes);
        if (mLoggerEvent != null) {
            if (t instanceof LoggerMessage) {
                LoggerMessage loggerMessage = (LoggerMessage) t;
                mLoggerEvent.onWrite(file, loggerMessage.mTag, loggerMessage.mMsg.toString());
            }
        }
    }

    @Override
    public void writeText(File file, String text) {
        byte[] bytes = text.getBytes();
        write(file, bytes);
        if (mLoggerEvent != null) {
            mLoggerEvent.onWriteText(file, text);
        }
    }

    private void write(File file, byte[] bytes) {
        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel = null;
        FileLock lock = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
//            System.out.println("size=" + size);
            lock = fileChannel.lock();
            long size = fileChannel.size();
            fileChannel.position(size);
//            System.out.println("v" + lock.isValid());
            ByteBuffer sendBuffer = ByteBuffer.wrap(bytes);
            fileChannel.write(sendBuffer);
//            Thread.sleep(100);

        } catch (FileNotFoundException e) {
            mLoggerEvent.onException(file, e);
        } catch (IOException e) {
            mLoggerEvent.onException(file, e);
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
//                    e.printStackTrace();
                    mLoggerEvent.onException(file, e);
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
//                    e.printStackTrace();
                    mLoggerEvent.onException(file, e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
//                e.printStackTrace();
                    mLoggerEvent.onException(file, e);
                }
            }
        }

    }

    @Override
    public void onPostWrite(File file, T t) {
        if (mLoggerEvent != null) {
            if (t instanceof LoggerMessage) {
                LoggerMessage loggerMessage = (LoggerMessage) t;
                mLoggerEvent.onPostWrite(file, loggerMessage.mTag, loggerMessage.mMsg.toString());
            }
        }
    }

    @Override
    public void flush() {

    }


}
