package com.example.schedulemanagement.utils;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/29
 *     desc   : 日程处理工具类
 * </pre>
 */

public class DateUtils {
    /**
     * 转换日期格式为2019-05-20
     * @param year 年
     * @param month 月
     * @param day 日
     * @return 返回2019-05-20这种格式的日期
     */
    public static String dateFormat(int year,int month,int day){
        Date date;
        GregorianCalendar gc = new GregorianCalendar();
        gc.set(Calendar.YEAR,year);
        gc.set(Calendar.MONTH,month-1);
        gc.set(Calendar.DAY_OF_MONTH,day);
        date = gc.getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
        return dateFm.format(date);
    }
    public static String getTodayDate(){
        Date date;
        GregorianCalendar gc = new GregorianCalendar();
        gc.get(Calendar.YEAR);
        gc.get(Calendar.MONTH);
        gc.get(Calendar.DAY_OF_MONTH);
        date = gc.getTime();
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFm = new SimpleDateFormat("yyyy-MM-dd");
        return dateFm.format(date);
    }

    public static String formatTime(int time){
        if(time<10){
            return "0"+String.valueOf(time);
        }
        return ""+time;
    }

    /**
     * 格式化时间
     * @param time
     * @return
     */
    public static String timeFormat(String time){
        return time.substring(0,5);
    }

    public static String dateFormat(String date){
        String[] dates = date.split("-");
        return dates[1]+"月"+dates[2]+"日";
    }
}
