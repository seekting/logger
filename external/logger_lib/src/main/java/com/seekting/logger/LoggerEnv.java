package com.seekting.logger;


/**
 * Created by Administrator on 2017/9/18.
 */

public class LoggerEnv {

    public static final String pattern = "yyyy-MM-dd-HH_mm_ss_SSS";
    public static final String DEFAULT_PID = "0";
    public static final String DEFAULT_PROCESS_NAME = "loggerOutput";
    public static final String LOGGER_OUTPUT_PATH = "/loggerOutput/";


    //every log max file length 10M
    public static final int DEFAULT_MAX_FILE_SIZE = 10 * 1024 * 1024;/*byte*/
    //    public static final int DEFAULT_MAX_FILE_SIZE = 8 * 10 * 10;/*byte*/
    //every log record recently 30minutes event
    public static final long DEFAULT_MAX_FILE_TIME = 30 * 60 * 1000;


    public static final long MIN_FILE_SIZE = 3 * 1024 * 1024;
    public static final long MIN_FILE_TIME = 5 * 60 * 1000;
}
