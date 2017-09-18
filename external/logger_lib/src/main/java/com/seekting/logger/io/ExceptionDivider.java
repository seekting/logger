package com.seekting.logger.io;

/**
 * Created by Administrator on 2017/9/18.
 */

public interface ExceptionDivider<T> {
    void dividerException(T t, Throwable throwable);

    void dividerException(Throwable throwable);
}
