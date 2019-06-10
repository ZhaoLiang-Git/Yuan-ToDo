package com.example.schedulemanagement.app;

import android.app.Application;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/04
 *     desc   : 自定义Application
 * </pre>
 */

public class App extends Application {
    private static App mApp;
    private static PersistentCookieStore mCookieStore;

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        mCookieStore = new PersistentCookieStore(getApplicationContext());
        CookieJarImpl cookieJar = new CookieJarImpl(mCookieStore);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(cookieJar)
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();

        OkHttpUtils.initClient(okHttpClient);
    }

    public static App getContext(){
        return mApp;
    }

    public static PersistentCookieStore getCookieStore(){
        return mCookieStore;
    }
}
