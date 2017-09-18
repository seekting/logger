package com.seekting.logger.callback;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LogEventWrapper implements LoggerEvent {


    private LoggerEvent mBase;

    public void attach(LoggerEvent base) {
        mBase = base;
    }

    @Override
    public void onPreWrite(File f, String tag, Object msg) {
        if (mBase != null) {
            mBase.onPreWrite(f, tag, msg);
        }

    }

    @Override
    public void onWrite(File f, String tag, Object msg) {
        if (mBase != null) {
            mBase.onWrite(f, tag, msg);
        }
    }

    @Override
    public void onPostWrite(File f, String tag, Object msg) {
        if (mBase != null) {
            mBase.onPostWrite(f, tag, msg);
        }
    }

    @Override
    public void dividerException(File f, Throwable t) {
        if (mBase != null) {
            mBase.dividerException(f, t);
        }
    }

    @Override
    public void dividerException(Throwable t) {
        if (mBase != null) {
            mBase.dividerException(t);
        }
    }

    @Override
    public void onWriteText(File file, String text) {
        if (mBase != null) {
            mBase.onWriteText(file, text);
        }
    }

    @Override
    public void onRecordLogcat(String realFileName) {
        if (mBase != null) {
            mBase.onRecordLogcat(realFileName);
        }
    }
}
