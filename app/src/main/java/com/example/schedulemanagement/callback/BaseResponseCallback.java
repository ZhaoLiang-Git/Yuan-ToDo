package com.example.schedulemanagement.callback;

import android.util.Log;

import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.example.schedulemanagement.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;


import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/05/04
 *     desc   : 回调BaseResponse对象
 * </pre>
 */

public abstract class BaseResponseCallback<T> extends Callback<BaseResponse<T>> {
    private static final String TAG = "BaseResponseCallback";
    @Override
    public BaseResponse<T> parseNetworkResponse(Response response, int id) throws Exception {
        Type type = new TypeToken<BaseResponse<T>>() {}.getType();
        Gson gson= new Gson() ;
        BaseResponse<T> baseResponse=gson.fromJson(response.body().string(), type);
        return baseResponse;
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        CommonUtils.showToast(e.toString());
        Log.d(TAG, "onError: "+e.toString());
        e.printStackTrace();
    }
}
