package com.example.schedulemanagement.utils;


import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.schedulemanagement.app.ConstData;

import java.util.Calendar;
import java.util.TimeZone;

/**
 * Created by Jimmy on 2016/10/6 0006.
 */
public class CalendarUtils {
    public static final int NUM_ROWS = 6;

    /**
     * 通过年份和月份 得到当月的日子
     *
     * @param year
     * @param month
     * @return
     */
    public static int getMonthDays(int year, int month) {
        month++;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                return 31;
            case 4:
            case 6:
            case 9:
            case 11:
                return 30;
            case 2:
                if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)) {
                    return 29;
                } else {
                    return 28;
                }
            default:
                return -1;
        }
    }

    /**
     * 返回当前月份1号位于周几
     *
     * @param year  年份
     * @param month 月份，传入系统获取的，不需要正常的
     * @return 日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getFirstDayWeek(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, 1);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回该日期位于周几
     */
    public static int getDayWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 返回该日期是否周末
     */
    public static boolean isWeekend(int year, int month, int day) {
        int dayWeek = getDayWeek(year, month, day);
        return Calendar.SUNDAY == dayWeek || Calendar.SATURDAY == dayWeek;
    }

    /**
     * 获得两个日期距离几周
     *
     * @param lastYear
     * @param lastMonth 月份从0开始
     * @param lastDay
     * @param year
     * @param month     月份从0开始
     * @param day
     * @return
     */
    public static int getWeeksAgo(int lastYear, int lastMonth, int lastDay, int year, int month, int day) {
        Calendar lastClickDay = Calendar.getInstance();
        lastClickDay.set(lastYear, lastMonth, lastDay);
        int week = lastClickDay.get(Calendar.DAY_OF_WEEK) - 1;
        Calendar clickDay = Calendar.getInstance();
        clickDay.set(year, month, day);
        if (clickDay.getTimeInMillis() > lastClickDay.getTimeInMillis()) {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + week * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        } else {
            return (int) ((clickDay.getTimeInMillis() - lastClickDay.getTimeInMillis() + (week - 6) * 24 * 3600 * 1000) / (7 * 24 * 3600 * 1000));
        }
    }

    /**
     * 获得两个日期距离几个月
     *
     * @return
     */
    public static int getMonthsAgo(int lastYear, int lastMonth, int year, int month) {
        return (year - lastYear) * 12 + (month - lastMonth);
    }

    public static int getWeekRow(int year, int month, int day) {
        int week = getFirstDayWeek(year, month);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        int lastWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (lastWeek == 7)
            day--;
        return (day + week - 1) / 7;
    }

    /**
     * 获取该月有几周
     */
    public static int getWeekRows(int year, int month) {
        int numRows;
        int monthDays = CalendarUtils.getMonthDays(year, month);
        int weekNumber = CalendarUtils.getFirstDayWeek(year, month);
        //上个月占的天数
        int lastMonthDays = weekNumber - 1;
        //下个月占的天数
        int nextMonthDays = 42 - monthDays - weekNumber + 1;
        numRows = NUM_ROWS - (lastMonthDays / 7 + nextMonthDays / 7);

        return numRows;
    }

    public static int getWeekDayCount(int startWeekDay, int endWeekDay) {
        int weekCount;
        if (endWeekDay >= startWeekDay) {
            weekCount = endWeekDay - startWeekDay;
        } else {
            weekCount = 7 + endWeekDay - startWeekDay;
        }
        return weekCount;
    }

    /**
     * 获取日历最小日期
     */
    public static Calendar getMinCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ConstData.MIN_YEAR);
        calendar.set(Calendar.MONTH, ConstData.MIN_MONTH - 1);
        calendar.set(Calendar.DAY_OF_MONTH, ConstData.MIN_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, ConstData.MIN_HOUR);
        calendar.set(Calendar.MINUTE, ConstData.MIN_MINUTE);
        calendar.set(Calendar.SECOND, ConstData.MIN_SECOND);
        return calendar;
    }

    /**
     * 获取日历最大日期
     */
    public static Calendar getMaxCalendar() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, ConstData.MAX_YEAR);
        calendar.set(Calendar.MONTH, ConstData.MAX_MONTH - 1);
        calendar.set(Calendar.DAY_OF_MONTH, ConstData.MAX_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, ConstData.MAX_HOUR);
        calendar.set(Calendar.MINUTE, ConstData.MAX_MINUTE);
        calendar.set(Calendar.SECOND, ConstData.MAX_SECOND);
        return calendar;
    }

    /**
     * 辅助方法：通过日程提醒id确认多少分钟前发出提醒
     *
     * @param remindid 传入日程提醒ID
     */
    public static int remindidTimeConvert(int remindid) {
        switch (remindid) {
            case 0:
            case 1:
                return 0;
            case 2:
                return 5;
            case 3:
                return 15;
            case 4:
                return 30;
            case 5:
                return 60;
            case 6:
                return 120;
            case 7:
                return 1440;
            case 8:
                return 2880;
            default:
                return -1;
        }
    }

    /**
     * 辅助方法：通过日程开始时间确认是否重复提醒设置重复规则
     *
     * @param alertTime   开始时间时间戳
     * @param endTimeMill 结束时间时间戳
     * @param allday      是否全天模式
     * @param repeatMode  日程重复模式，0是单个重复（永不、每天、每周...），1是自定义重复（周日、周一...）
     * @param repeatId    日程重复id，repeatMode=0时：0永不，1每天，2每周，3每月，4每年；repeatMode=1时：0周日，1周一，2周二，3周三，4周四，5周五，6周六
     */
    public static String repeatRrule(long alertTime, long endTimeMill, int allday, int repeatMode, String repeatId,int isNonli) {
        String mRule = "";
        if (1 == isNonli) {
            return mRule = null;
        }
        String tipTime = DateFormatter.getTimeToFormat(alertTime);
        String[] split = tipTime.split(" ");
        String[] dateStr = split[0].split("-");
        int month = Integer.parseInt(dateStr[1]);
        int day = Integer.parseInt(dateStr[2]);

        String until = "20241231T235959Z";//截止时间：2024年12月31日23点59分59秒


        if (repeatMode == 0) {
            switch (repeatId) {
                case "0"://不重复
                    mRule = null;
                    break;
                case "1"://按天（每天）
                    mRule = "FREQ=DAILY;UNTIL=" + until;
                    break;
                case "4"://按年重复(每年的某月某日)
                    mRule = "FREQ=YEARLY;UNTIL=" + until + ";WKST=SU;BYMONTH=" + month + ";BYMONTHDAY=" + day;
                    break;
                case "3"://按月重复（每月的某天）
                    mRule = "FREQ=MONTHLY;UNTIL=" + until + ";WKST=SU;BYMONTHDAY=" + day;
                    break;
                case "2"://按周重复
                    String week = DateFormatter.getWeekDay(alertTime);
                    switch (week) {
                        case "周日":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=SU";
                            break;
                        case "周一":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=MO";
                            break;
                        case "周二":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=TU";
                            break;
                        case "周三":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=WE";
                            break;
                        case "周四":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=TH";
                            break;
                        case "周五":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=FR";
                            break;
                        case "周六":
                            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=SA";
                            break;
                    }
                    break;
            }
        } else if (repeatMode == 1) {
            //自定义周几重复，可多选
            mRule = "FREQ=WEEKLY;UNTIL=" + until + ";WKST=SU;BYDAY=";
            String[] weeks = repeatId.split(",");
            for (int i = 0; i < weeks.length; i++) {
                if (weeks[i].equals("0")) {
                    mRule += "SU,";
                } else if (weeks[i].equals("1")) {
                    mRule += "MO,";
                } else if (weeks[i].equals("2")) {
                    mRule += "TU,";
                } else if (weeks[i].equals("3")) {
                    mRule += "WE,";
                } else if (weeks[i].equals("4")) {
                    mRule += "TH,";
                } else if (weeks[i].equals("5")) {
                    mRule += "FR,";
                } else if (weeks[i].equals("6")) {
                    mRule += "SA,";
                }
            }
            if (mRule.endsWith(",")) {
                mRule.substring(0, mRule.length() - 1);
            }
        }
        return mRule;
    }
}

