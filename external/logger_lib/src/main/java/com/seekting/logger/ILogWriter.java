package com.seekting.logger;

import java.io.File;

/**
 * Created by Administrator on 2017/8/31.
 */

public interface ILogWriter<T> {

    void preWrite(File f, T t);

    void doWrite(File f, T t);

    void onPostWrite(File f, T t);

    void flush();

    void writeText(File f, String text);


}
