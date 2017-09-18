package com.seekting.logger.io;


import com.seekting.logger.callback.LoggerEvent;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LogWriterWrapper implements ILogWriter, ExceptionDivider<File> {
    private LogWriter mBase;
    private LoggerEvent loggerEvent;

    public void attach(LogWriter logWriterAdapter, LoggerEvent loggerEvent) {
        mBase = logWriterAdapter;
        this.loggerEvent = loggerEvent;
        if (mBase != null) {
            mBase.setExceptionDivider(this);
        }

    }


    @Override
    public void preWrite(File f, String tag, Object msg) {
        if (loggerEvent != null) {
            loggerEvent.onPreWrite(f, tag, msg);
        }
        if (mBase != null) {
            mBase.preWrite(f, tag, msg);
        }

    }

    @Override
    public void doWrite(File f, String tag, Object msg, String line) {

        if (loggerEvent != null) {
            loggerEvent.onWrite(f, tag, msg);
        }
        if (mBase != null) {
            mBase.doWrite(f, tag, msg, line);
        }
    }

    @Override
    public void onPostWrite(File f, String tag, Object msg) {
        if (loggerEvent != null) {
            loggerEvent.onPostWrite(f, tag, msg);
        }
        if (mBase != null) {
            mBase.onPostWrite(f, tag, msg);
        }
    }

    @Override
    public void flush() {
        if (mBase != null) {
            mBase.flush();
        }
    }

    @Override
    public void writeText(File f, String text) {
        if (loggerEvent != null) {
            loggerEvent.onWriteText(f, text);
        }
        if (mBase != null) {
            mBase.writeText(f, text);
        }
    }


    @Override
    public void dividerException(File f, Throwable t) {
        if (loggerEvent != null) {
            loggerEvent.dividerException(f, t);
        }
    }

    @Override
    public void dividerException(Throwable throwable) {
        if (loggerEvent != null) {
            loggerEvent.dividerException(throwable);
        }
    }
}
