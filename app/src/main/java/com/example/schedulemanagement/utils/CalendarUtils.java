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
    private static String calanderURL;
    private static String calanderEventURL;
    private static String calanderRemiderURL;
    private static String CALENDARS_NAME = "smartTime";
    private static String CALENDARS_ACCOUNT_NAME = "smartTime@smartTime.com";
    private static String CALENDARS_ACCOUNT_TYPE = "com.zg.smartTime";//LOCAL
    private static String CALENDARS_DISPLAY_NAME = "smartTime账号";
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

    //////////////////////////////////////////////////////////////向系统日历添加日程提醒事件///////////////////////////////////////////////////////

    /**
     * 初始化uri
     */
    static {
        if (Build.VERSION.SDK_INT >= 8) {
            calanderURL = "content://com.android.calendar/calendars";
            calanderEventURL = "content://com.android.calendar/events";
            calanderRemiderURL = "content://com.android.calendar/reminders";
        } else {
            calanderURL = "content://calendar/calendars";
            calanderEventURL = "content://calendar/events";
            calanderRemiderURL = "content://calendar/reminders";
        }
    }

    /**
     * 获取日历ID
     *
     * @param context
     * @return 日历ID
     */
    public static int checkAndAddCalendarAccounts(Context context) {
        int oldId = checkCalendarAccounts(context);
        if (oldId >= 0) {
            return oldId;
        } else {
            long addId = addCalendarAccount(context);
            if (addId >= 0) {
                return checkCalendarAccounts(context);
            } else {
                return -1;
            }
        }
    }

    /**
     * 检查是否存在日历账户
     *
     * @param context
     * @return
     */
    private static int checkCalendarAccounts(Context context) {
        Cursor userCursor = context.getContentResolver().query(Uri.parse(calanderURL), null, null, null, CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL + " ASC ");
        try {
            if (userCursor == null)//查询返回空值
                return -1;
            int count = userCursor.getCount();
            if (count > 0) {//存在现有账户，取第一个账户的id返回
                userCursor.moveToLast();
                return userCursor.getInt(userCursor.getColumnIndex(CalendarContract.Calendars._ID));
            } else {
                return -1;
            }
        } finally {
            if (userCursor != null) {
                userCursor.close();
            }
        }
    }

    /**
     * 添加一个日历账户
     *
     * @param context
     * @return
     */
    private static long addCalendarAccount(Context context) {
        TimeZone timeZone = TimeZone.getDefault();
        ContentValues value = new ContentValues();
        value.put(CalendarContract.Calendars.NAME, CALENDARS_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE);
        value.put(CalendarContract.Calendars.CALENDAR_DISPLAY_NAME, CALENDARS_DISPLAY_NAME);
        //可见度
        value.put(CalendarContract.Calendars.VISIBLE, 1);
        //日历颜色
        value.put(CalendarContract.Calendars.CALENDAR_COLOR, Color.BLUE);
        //权限
        value.put(CalendarContract.Calendars.CALENDAR_ACCESS_LEVEL, CalendarContract.Calendars.CAL_ACCESS_OWNER);
        value.put(CalendarContract.Calendars.SYNC_EVENTS, 1);
        //时区
        value.put(CalendarContract.Calendars.CALENDAR_TIME_ZONE, timeZone.getID());
        value.put(CalendarContract.Calendars.OWNER_ACCOUNT, CALENDARS_ACCOUNT_NAME);
        value.put(CalendarContract.Calendars.CAN_ORGANIZER_RESPOND, 0);

        Uri calendarUri = Uri.parse(calanderURL);
        calendarUri = calendarUri.buildUpon()
                .appendQueryParameter(CalendarContract.CALLER_IS_SYNCADAPTER, "true")
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_NAME, CALENDARS_ACCOUNT_NAME)
                .appendQueryParameter(CalendarContract.Calendars.ACCOUNT_TYPE, CALENDARS_ACCOUNT_TYPE)
                .build();
        Uri result = context.getContentResolver().insert(calendarUri, value);
        long id = result == null ? -1 : ContentUris.parseId(result);
        return id;
    }

    /**
     * 向日历中添加一个事件
     *
     * @param context
     * @param calendar_id （必须参数）
     * @param title
     * @param description
     * @param begintime   事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。 （必须参数）
     * @param endtime     事件结束时间，以从公元纪年开始计算的协调世界时毫秒数表示。（非重复事件：必须参数）
     * @return
     */
    private static Uri insertCalendarEvent(Context context, long calendar_id, String title, String description, long begintime, long endtime) {
        ContentValues event = new ContentValues();
        event.put("title", title);
        event.put("description", description);
        // 插入账户的id
        event.put("calendar_id", calendar_id);
        event.put(CalendarContract.Events.DTSTART, begintime);//必须有
        event.put(CalendarContract.Events.DTEND, endtime);//非重复事件：必须有
        event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
        event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());//这个是时区，必须有，
        //添加事件
        Uri newEvent = context.getContentResolver().insert(Uri.parse(calanderEventURL), event);
        return newEvent;
    }

    /**
     * 查询日历事件
     *
     * @param context
     * @param title   事件标题
     * @return 事件id, 查询不到则返回""
     */
    public static String queryCalendarEvent(Context context, long calendar_id, String title, String description, long start_time, long end_time) {
        // 根据日期范围构造查询
        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        ContentUris.appendId(builder, start_time);
        ContentUris.appendId(builder, end_time);
        Cursor cursor = context.getContentResolver().query(builder.build(), null, null, null, null);
        String tmp_title;
        String tmp_desc;
        long temp_calendar_id;
        if (cursor.moveToFirst()) {
            do {
                tmp_title = cursor.getString(cursor.getColumnIndex("title"));
                tmp_desc = cursor.getString(cursor.getColumnIndex("description"));
                temp_calendar_id = cursor.getLong(cursor.getColumnIndex("calendar_id"));
                long dtstart = cursor.getLong(cursor.getColumnIndex("dtstart"));
                if (TextUtils.equals(title, tmp_title) && TextUtils.equals(description, tmp_desc) && calendar_id == temp_calendar_id && dtstart == start_time) {
                    String eventId = cursor.getString(cursor.getColumnIndex("event_id"));
                    return eventId;
                }
            } while (cursor.moveToNext());
        }
        return "";
    }

    /**
     * 添加日历提醒：标题、描述、开始时间共同标定一个单独的提醒事件
     *
     * @param context
     * @param title          日历提醒的标题,不允许为空
     * @param description    日历的描述（备注）信息
     * @param begintime      事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。
     * @param endtime        事件结束时间，以从公元纪年开始计算的协调世界时毫秒数表示。
     * @param remind_minutes 提前remind_minutes分钟发出提醒
     * @param callback       添加提醒是否成功结果监听
     */
    public static void addCalendarEventRemind(Context context, @NonNull String title, String description, long begintime, long endtime, int remind_minutes, onCalendarRemindListener callback) {
        long calendar_id = checkAndAddCalendarAccounts(context);
        if (calendar_id < 0) {
            // 获取日历失败直接返回
            if (null != callback) {
                callback.onFailed(onCalendarRemindListener.Status._CALENDAR_ERROR);
            }
            return;
        }
        //根据标题、描述、开始时间查看提醒事件是否已经存在
        String event_id = queryCalendarEvent(context, calendar_id, title, description, begintime, endtime);
        //如果提醒事件不存在，则新建事件
        if (TextUtils.isEmpty(event_id)) {
            Uri newEvent = insertCalendarEvent(context, calendar_id, title, description, begintime, endtime);
            if (newEvent == null) {
                // 添加日历事件失败直接返回
                if (null != callback) {
                    callback.onFailed(onCalendarRemindListener.Status._EVENT_ERROR);
                }
                return;
            }
            event_id = ContentUris.parseId(newEvent) + "";
        }
        //为事件设定提醒
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, event_id);
        // 提前remind_minutes分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, remind_minutes);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        Uri uri = context.getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
        if (uri == null) {
            // 添加提醒失败直接返回
            if (null != callback) {
                callback.onFailed(onCalendarRemindListener.Status._REMIND_ERROR);
            }
            return;
        }
        //添加提醒成功
        if (null != callback) {
            callback.onSuccess();
        }
    }

    /**
     * 添加日历提醒：标题、描述、开始时间共同标定一个单独的提醒事件
     *
     * @param context
     * @param title          日历提醒的标题,不允许为空
     * @param description    日历的描述（备注）信息
     * @param begintime      事件开始时间，以从公元纪年开始计算的协调世界时毫秒数表示。
     * @param endtime        事件结束时间，以从公元纪年开始计算的协调世界时毫秒数表示。
     * @param remind_minutes 提前remind_minutes分钟发出提醒
     * @param rules          重复规则
     * @param callback       添加提醒是否成功结果监听
     */
    public static void addCalendarEventRemind(Context context, @NonNull String title, String description, long begintime, long endtime, int remind_minutes, String rules, onCalendarRemindListener callback) {
        long calendar_id = checkAndAddCalendarAccounts(context);
        if (calendar_id < 0) {
            // 获取日历失败直接返回
            if (null != callback) {
                callback.onFailed(onCalendarRemindListener.Status._CALENDAR_ERROR);
            }
            return;
        }
        //根据标题、描述、开始时间查看提醒事件是否已经存在
        String event_id = queryCalendarEvent(context, calendar_id, title, description, begintime, endtime);
        //如果提醒事件不存在，则新建事件
        if (TextUtils.isEmpty(event_id)) {
            ContentValues event = new ContentValues();
            event.put("title", title);
            event.put("description", description);
            // 插入账户的id
            event.put("calendar_id", calendar_id);
            event.put(CalendarContract.Events.RRULE, rules);
            event.put(CalendarContract.Events.DTSTART, begintime);//必须有
            event.put(CalendarContract.Events.DTEND, endtime);//非重复事件：必须有
            event.put(CalendarContract.Events.HAS_ALARM, 1);//设置有闹钟提醒
            event.put(CalendarContract.Events.EVENT_TIMEZONE, TimeZone.getDefault().getID());//这个是时区，必须有，
            //添加事件
            Uri newEvent = context.getContentResolver().insert(Uri.parse(calanderEventURL), event);
            if (newEvent == null) {
                // 添加日历事件失败直接返回
                if (null != callback) {
                    callback.onFailed(onCalendarRemindListener.Status._EVENT_ERROR);
                }
                return;
            }
            event_id = ContentUris.parseId(newEvent) + "";
        }
        //为事件设定提醒
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Reminders.EVENT_ID, event_id);
        // 提前remind_minutes分钟有提醒
        values.put(CalendarContract.Reminders.MINUTES, remind_minutes);
        values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
        // values.put(CalendarContract.EXTRA_EVENT_ALL_DAY, isAllDay);
        Uri uri = context.getContentResolver().insert(Uri.parse(calanderRemiderURL), values);
        if (uri == null) {
            // 添加提醒失败直接返回
            if (null != callback) {
                callback.onFailed(onCalendarRemindListener.Status._REMIND_ERROR);
            }
            return;
        }
        //添加提醒成功
        if (null != callback) {
            callback.onSuccess();
        }
    }

    /**
     * 删除日历提醒事件：根据标题、描述和开始时间来定位日历事件
     *
     * @param context
     * @param title       提醒的标题
     * @param description 提醒的描述：deeplink URI
     * @param startTime   事件的开始时间
     * @param callback    删除成功与否的监听回调
     */
    public static void deleteCalendarEventRemind(Context context, String title, String description, long startTime, onCalendarRemindListener callback) {
        Cursor eventCursor = context.getContentResolver().query(Uri.parse(calanderEventURL), null, null, null, null);
        Log.i("zxd", "deleteCalendarEventRemind: " + (eventCursor == null));
        try {
            if (eventCursor == null)//查询返回空值
                return;
            if (eventCursor.getCount() > 0) {
                //遍历所有事件，找到title、description、startTime跟需要查询的title、descriptio、dtstart一样的项
                for (eventCursor.moveToFirst(); !eventCursor.isAfterLast(); eventCursor.moveToNext()) {
                    String eventTitle = eventCursor.getString(eventCursor.getColumnIndex("title"));
                    String eventDescription = eventCursor.getString(eventCursor.getColumnIndex("description"));
                    long dtstart = eventCursor.getLong(eventCursor.getColumnIndex("dtstart"));
                    if (!TextUtils.isEmpty(title) && title.equals(eventTitle) && !TextUtils.isEmpty(description) && description.equals(eventDescription) && dtstart == startTime) {
                        int id = eventCursor.getInt(eventCursor.getColumnIndex(CalendarContract.Calendars._ID));//取得id
                        Uri deleteUri = ContentUris.withAppendedId(Uri.parse(calanderEventURL), id);
                        int rows = context.getContentResolver().delete(deleteUri, null, null);
                        if (rows == -1) {
                            // 删除提醒失败直接返回
                            if (null != callback) {
                                callback.onFailed(onCalendarRemindListener.Status._REMIND_ERROR);
                            }
                            return;
                        }
                        //删除提醒成功
                        if (null != callback) {
                            callback.onSuccess();
                        }
                    }
                }
            }
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
    }

    /**
     * 日历提醒添加成功与否监控器
     */
    public static interface onCalendarRemindListener {
        enum Status {
            _CALENDAR_ERROR,
            _EVENT_ERROR,
            _REMIND_ERROR
        }

        void onFailed(Status error_code);

        void onSuccess();
    }

    /**
     * 辅助方法：获取设置时间起止时间的对应毫秒数
     *
     * @param year
     * @param month  1-12
     * @param day    1-31
     * @param hour   0-23
     * @param minute 0-59
     * @return
     */
    public static long remindTimeCalculator(int year, int month, int day, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day, hour, minute, 0);
        calendar.set(Calendar.MILLISECOND, 0);//要保持数据一致。如果你的毫秒数不一样，即使日历事件的标题和内容一样也是不能删除的
        return calendar.getTimeInMillis();
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
    public static String repeatRrule(long alertTime, long endTimeMill, int allday, int repeatMode, String repeatId) {
        String tipTime = DateFormatter.getTimeToFormat(alertTime);
//        long start = alertTime;
//        long end = endTimeMill;// start + 60 * 60 * 1000L;
        String[] split = tipTime.split(" ");
        String[] dateStr = split[0].split("-");
        int month = Integer.parseInt(dateStr[1]);
        int day = Integer.parseInt(dateStr[2]);

        String until = "20241231T235959Z";//截止时间：2024年12月31日23点59分59秒
        String mRule = "";

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
    //////////////////////////////////////////////////////////////END///////////////////////////////////////////////////////
}

