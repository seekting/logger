package com.seekting.logger.io;

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

public class RandomAccessWriter extends LogWriter {


    @Override
    public void doWrite(File file, String tag, Object msg, String line) {
        super.doWrite(file, tag, msg, line);
        byte[] bytes = line.getBytes();
        write(file, bytes);
    }

    @Override
    public void writeText(File file, String text) {
        super.writeText(file, text);
        byte[] bytes = text.getBytes();
        write(file, bytes);
    }

    private void write(File file, byte[] bytes) {
        RandomAccessFile randomAccessFile = null;
        FileChannel fileChannel = null;
        FileLock lock = null;
        try {
            randomAccessFile = new RandomAccessFile(file, "rw");
            fileChannel = randomAccessFile.getChannel();
            lock = fileChannel.lock();
            long size = fileChannel.size();
            fileChannel.position(size);
            ByteBuffer sendBuffer = ByteBuffer.wrap(bytes);
            fileChannel.write(sendBuffer);

        } catch (FileNotFoundException e) {
            onException(file, e);
        } catch (IOException e) {
            onException(file, e);
        } finally {
            if (lock != null) {
                try {
                    lock.release();
                } catch (IOException e) {
                    onException(file, e);
                }
            }
            if (fileChannel != null) {
                try {
                    fileChannel.close();
                } catch (IOException e) {
                    onException(file, e);
                }
            }
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    onException(file, e);
                }
            }
        }

    }


}
