package com.example.schedulemanagement.utils;

import android.content.Context;
import android.widget.Toast;

import com.example.schedulemanagement.app.App;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/04
 *     desc   : 公用的工具类
 * </pre>
 */

public class CommonUtils {
    /**
     * 吐司提示
     * @param message 提示的内容
     */
    public static void showToast(String message){
        Toast.makeText(App.getContext(),message,Toast.LENGTH_SHORT).show();
    }
    public static void showToast(Context context,String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
}
