package com.seekting.logger.io;

import java.io.File;

/**
 * Created by Administrator on 2017/9/18.
 */

public class LogWriter implements ILogWriter {
    protected ExceptionDivider mExceptionDivider;


    public void setExceptionDivider(ExceptionDivider exceptionDivider) {
        mExceptionDivider = exceptionDivider;
    }

    @Override
    public void preWrite(File f, String tag, Object msg) {

    }

    @Override
    public void doWrite(File f, String tag, Object msg, String line) {

    }

    @Override
    public void onPostWrite(File f, String tag, Object msg) {

    }

    @Override
    public void flush() {

    }

    @Override
    public void writeText(File f, String text) {

    }

    protected void onException(File f, Throwable t) {
        if (mExceptionDivider != null) {
            mExceptionDivider.dividerException(f, t);
        }


    }


}
