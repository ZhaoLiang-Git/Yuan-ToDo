package com.example.schedulemanagement.callback;

import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/12
 *     desc   :
 * </pre>
 */

public abstract class LoginAndRegisterCallback extends Callback<BaseResponse<LoginAndRegister>> {
    @Override
    public BaseResponse<LoginAndRegister> parseNetworkResponse(Response response, int id) throws Exception {
        Type type = new TypeToken<BaseResponse<LoginAndRegister>>() {}.getType();
        Gson gson= new Gson() ;
        return gson.fromJson(response.body().string(), type);
    }
}
