package com.example.schedulemanagement.callback;

import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.entity.Event;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.Callback;

import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

/**
 * <pre>
 *     author : 残渊
 *     time   : 2019/06/03
 *     desc   :
 * </pre>
 */

public abstract class EventResponseCallback extends Callback<BaseResponse<Event>> {
    @Override
    public BaseResponse<Event> parseNetworkResponse(Response response, int id) throws Exception {
        Type type = new TypeToken<BaseResponse<Event>>() {}.getType();
        Gson gson= new Gson() ;
        return gson.fromJson(response.body().string(), type);
    }
}
