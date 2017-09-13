package com.seekting.logger;

import java.io.File;

/**
 * Created by Administrator on 2017/8/31.
 */

public interface LoggerEvent {

    void onPreWrite(File f, String tag, String msg);

    void onWrite(File f, String tag, String msg);

    void onPostWrite(File f, String tag, String msg);

    void onException(File f, Throwable t);

    void onWriteText(File file, String text);

    void onRecordLogcat(String realFileName);

    void onClear(String s);
}
