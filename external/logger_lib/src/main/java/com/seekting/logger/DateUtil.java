package com.seekting.logger;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2017/9/20.
 */

public class DateUtil {
    private static final ThreadLocal<DateFormatWrapper> sThreadLocal = new ThreadLocal<>();

    public static Date parse(String input) throws ParseException {
        DateFormatWrapper dateFormatWrapper = obtainDateFormat();
        return dateFormatWrapper.parse(input);

    }

    public static String format() {
        return format(System.currentTimeMillis());

    }


    public static String format(long time) {
        DateFormatWrapper dateFormatWrapper = obtainDateFormat();

        return dateFormatWrapper.format(time);

    }

    private static DateFormatWrapper obtainDateFormat() {
        DateFormatWrapper dateFormatWrapper = sThreadLocal.get();
        if (dateFormatWrapper == null) {
            dateFormatWrapper = new DateFormatWrapper();
            sThreadLocal.set(dateFormatWrapper);
        }
        return dateFormatWrapper;
    }

    private static class DateFormatWrapper {
        public static final String PATTERN = "yyyy-MM-dd-HH_mm_ss_SSS";
        private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat(PATTERN);
        private Date mDate = new Date();


        public Date parse(String input) throws ParseException {
            Date result = mSimpleDateFormat.parse(input);
            return result;
        }

        public String format() {
            return format(System.currentTimeMillis());
        }

        public String format(long time) {
            mDate.setTime(time);
            return mSimpleDateFormat.format(mDate);
        }
    }
}
