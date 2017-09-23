package com.lmachine.mlda.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by SailFlorve on 2017/8/28 0028.
 * 时间工具。
 */

public class TimeUtil {
    public static final String A = "yyyy年MM月dd日 HH:mm:ss";
    public static final String B = "yyyy-MM-dd HH:mm:ss";
    public static final String C = "MM月dd日 HH:mm:ss";
    public static final String D = "MM-dd HH:mm:ss";
    public static final String E = "yyyy-MM-dd HH-mm-ss";

    public static String getNowTime(String pattern) {
        return new SimpleDateFormat(pattern, Locale.getDefault()).format(new Date());
    }
}
