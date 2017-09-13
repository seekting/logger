package com.seekting.logger;

/**
 * Created by Administrator on 2017/8/31.
 */

public abstract class BaseLogWriter<T> implements ILogWriter<T> {

    protected LoggerEvent mLoggerEvent;

    public BaseLogWriter(LoggerEvent loggerEvent) {
        mLoggerEvent = loggerEvent;
    }


}
