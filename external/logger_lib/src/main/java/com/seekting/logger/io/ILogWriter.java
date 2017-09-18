package com.seekting.logger.io;

import java.io.File;

/**
 * Created by Administrator on 2017/8/31.
 */

interface ILogWriter {

    void preWrite(File f, String tag, Object msg);

    void doWrite(File f, String tag, Object msg, String line);

    void onPostWrite(File f, String tag, Object msg);

    void flush();

    void writeText(File f, String text);


}
