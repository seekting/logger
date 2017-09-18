package com.seekting.logger.callback;


import com.seekting.logger.io.ExceptionDivider;

import java.io.File;

/**
 * Created by Administrator on 2017/8/31.
 */

public interface LoggerEvent extends ExceptionDivider<File> {

    void onPreWrite(File f, String tag, Object msg);

    void onWrite(File f, String tag, Object msg);

    void onPostWrite(File f, String tag, Object msg);


    void onWriteText(File file, String text);

    void onRecordLogcat(String realFileName);
}
