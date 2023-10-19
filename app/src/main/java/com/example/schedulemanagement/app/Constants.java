package com.example.schedulemanagement.app;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/02
 *     desc   : 常量类
 * </pre>
 */

public class Constants {
    public static final String BASE_URL="http://192.168.43.118:8080/Memo/";
    public static final String BASE_URL_MAIN="http://192.168.43.118:8080/Memo/main/";

//    public static final String BASE_URL="http://192.168.160.1:8080/Memo/";
//    public static final String BASE_URL_MAIN="http://192.168.160.1:8080/Memo/main/";
    public static final String Params_ID="id";
    public static final String Params_TITLE="title";
    public static final String Params_CONTENT="content";
    public static final String Params_PRIORITY="priority";
    public static final String Params_DATE="s_date";
    public static final String Params_STATUS="status";
    public static final String Params_START="s_starting";

    //IP地址
    public static final String WIFI_IP = "192.168.23.135";
    public static final String HOST_IP="192.168.69.2";
    public static final String DB_IP="127.0.0.1";


    //数据库
    public static final String DB_NAME="myw";

    //table
    public static final String TABLE_USER = "user_table";
    public static final String TABLE_TASK = "task_table";
    public static final String TABLE_TAG = "tag_table";
    public static final String TABLE_SELECT = "task_select_table";
    public static final String TABLE_CATEGORY = "category_select_table";//分类表




    public static final int CODE_SUCCESS=0;
    public static final int CODE_FAIL=0;

    public static final String KEY_ADD_DATE="date";
    public static final String KEY_ADD_DATE_FORMAT="date_format";
    public static final String KEY_SELECT_DATA="date_select_long";

    public static final String KEY_ID ="id";
    public static final String KEY_NAME ="name";
    public static final String KEY_SCHEDULE="schedule";
    public static final String KEY_TASK="task";
    public static final String KEY_TYPE="type";

    public static final int ADD = -1;
    public static final int TYPE_CATEGORY=0;
    public static final int TYPE_TAG=1;



}
