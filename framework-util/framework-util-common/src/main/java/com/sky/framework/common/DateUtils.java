package com.sky.framework.common;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期工具类, 继承org.apache.commons.lang.time.DateUtils类
 *
 * @author
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils {

    protected static Logger logger = LoggerFactory.getLogger(DateUtils.class);

    // 一天的开始时间
    public static String DATE_HH_mm_ss_START = "00:00:00";
    // 一天的结束时间
    public static String DATE_HH_mm_ss_END = "23:59:59";
    public static String DATE_yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static String DATE_yyyyMMdd_HH_mm_ss = "yyyyMMdd HH:mm:ss";
    public static String DATE_yyyy_MM_dd = "yyyy-MM-dd";
    public static String DATE_yyyyMMdd = "yyyyMMdd";
    public static String DATE_yyyyMMdd1 = "yyyy/MM/dd";
    public static String DATE_yyyyMMddHHmmss = "yyyyMMddHHmmss";
    // 一天的开始时间

    public static String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM",
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM"};

    private static Calendar gregorianCalendar = null;

    static {
        gregorianCalendar = new GregorianCalendar();
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     */
    public static String getDate() {
        return getDate("yyyy-MM-dd");
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String getDate(String pattern) {
        return DateFormatUtils.format(new Date(), pattern);
    }

    /**
     * 得到日期字符串 默认格式（yyyy-MM-dd） pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E"
     */
    public static String formatDate(Date date, Object... pattern) {
        String formatDate = null;
        if (pattern != null && pattern.length > 0) {
            formatDate = DateFormatUtils.format(date, pattern[0].toString());
        } else {
            formatDate = DateFormatUtils.format(date, "yyyy-MM-dd");
        }
        return formatDate;
    }

    /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return formatDate(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前时间字符串 格式（HH:mm:ss）
     */
    public static String getTime() {
        return formatDate(new Date(), "HH:mm:ss");
    }

    /**
     * 得到当前日期和时间字符串 格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String getDateTime() {
        return formatDate(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 得到当前年份字符串 格式（yyyy）
     */
    public static String getYear() {
        return formatDate(new Date(), "yyyy");
    }

    /**
     * 得到当前月份字符串 格式（MM）
     */
    public static String getMonth() {
        return formatDate(new Date(), "MM");
    }

    /**
     * 得到当天字符串 格式（dd）
     */
    public static String getDay() {
        return formatDate(new Date(), "dd");
    }

    /**
     * 得到当前星期字符串 格式（E）星期几
     */
    public static String getWeek() {
        return formatDate(new Date(), "E");
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     * "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     * "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date parseDate(Object str) {
        if (str == null) {
            return null;
        }
        try {
            return parseDate(str.toString(), parsePatterns);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 获取过去的天数
     *
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (24 * 60 * 60 * 1000);
    }

    /**
     * 获取过去的小时
     *
     * @param date
     * @return
     */
    public static long pastHour(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 60 * 1000);
    }

    /**
     * 获取过去的分钟
     *
     * @param date
     * @return
     */
    public static long pastMinutes(Date date) {
        long t = new Date().getTime() - date.getTime();
        return t / (60 * 1000);
    }

    /**
     * 转换为时间（天,时:分:秒.毫秒）
     *
     * @param timeMillis
     * @return
     */
    public static String formatDateTime(long timeMillis) {
        long day = timeMillis / (24 * 60 * 60 * 1000);
        long hour = (timeMillis / (60 * 60 * 1000) - day * 24);
        long min = ((timeMillis / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (timeMillis / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long sss = (timeMillis - day * 24 * 60 * 60 * 1000 - hour * 60 * 60 * 1000 - min * 60 * 1000 - s * 1000);
        return (day > 0 ? day + "," : "") + hour + ":" + min + ":" + s + "." + sss;
    }

    /**
     * 获取两个日期之间的天数
     *
     * @param before
     * @param after
     * @return
     */
    public static double getDistanceOfTwoDate(Date before, Date after) {
        long beforeTime = before.getTime();
        long afterTime = after.getTime();
        return (afterTime - beforeTime) / (1000 * 60 * 60 * 24);
    }

    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateString 特定格式的日期
     * @param format     格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static Date parse(String dateString, String format) {
        try {
            return (new SimpleDateFormat(format)).parse(dateString);
        } catch (ParseException e) {
            logger.error("Parse " + dateString + " with format " + format + " error!", e);
        }
        return null;
    }

    /**
     * 获取前一天时间，根据标识生成时间
     * 若为开始时间类型，根据标识自动生成 2017-04-08 00:00:00
     * 若为结束时间类型，根据标识自动生成 2017-04-08 23:59:59
     *
     * @param timeType
     * @return
     */
    public static String getDailyTime(String timeType, int intervalDay) {
        Date nowDate = DateUtils.addDays(new Date(), intervalDay);
        String date = dateFormatStr(nowDate, DATE_yyyy_MM_dd) + " %s";
        return TIME_TYPE_START.equals(timeType) ? String.format(date, DATE_HH_mm_ss_START) : String.format(date, DATE_HH_mm_ss_END);
    }

    //定义开始时间类型，根据标识自动生成 2017-04-08 00:00:00
    public static String TIME_TYPE_START = "START";
    //定义结束时间类型，根据标识自动生成 2017-04-08 23:59:59
    public static String TIME_TYPE_END = "END";

    public static String dateFormatStr(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        if (date == null || StringUtils.isBlank(format)) {
            return null;
        }
        dateFormat.applyPattern(format);
        String dateStr = null;
        dateStr = dateFormat.format(date);
        return dateStr;
    }

    /**
     * 获取加上指定天数的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date addDays(Date date, int days) {
        return add(date, Calendar.DATE, days);

    }

    /**
     * 获取减去指定天数的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date minusDays(Date date, int days) {
        return add(date, Calendar.DATE, -1 * days);
    }

    /**
     * 获取加上指定时间单位的日期
     *
     * @param date
     * @param days
     * @return
     */
    public static Date add(Date date, int field, int amount) {
        Calendar cad = Calendar.getInstance();
        cad.setTime(date);
        cad.add(field, amount);
        return cad.getTime();
    }

    /**
     * 获取当前时间戳
     *
     * @return
     */
    public static int getNowTime() {
        Date now = Calendar.getInstance().getTime();
        int nowTime = (int) (now.getTime() / 1000);
        return nowTime;
    }


    /**
     * 获取当前月的第一天
     *
     * @return date
     */
    public static Date getFirstDayOfMonth() {
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        return gregorianCalendar.getTime();
    }

    /**
     * 获取当前月的最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        gregorianCalendar.setTime(new Date());
        gregorianCalendar.set(Calendar.DAY_OF_MONTH, 1);
        gregorianCalendar.add(Calendar.MONTH, 1);
        gregorianCalendar.add(Calendar.DAY_OF_MONTH, -1);
        return gregorianCalendar.getTime();
    }

}
