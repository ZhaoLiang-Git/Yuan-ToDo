package com.example.schedulemanagement.view.activity;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.zhy.http.okhttp.OkHttpUtils;

public class LeadActivity extends AppCompatActivity {
    private static final String TAG = "LeadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
//        toLoginActivity();
//        ck();
      toMainActivity("袁健策3117004905");
    }
    private void ck(){
        OkHttpUtils.post()
                .url(Constants.BASE_URL+"ck")
                .build()
                .execute(new BaseResponseCallback<LoginAndRegister>() {
                    @Override
                    public void onResponse(BaseResponse<LoginAndRegister> response, int id) {
                        Log.d(TAG, "onResponse: ");
                        if(response.getCode() == Constants.CODE_SUCCESS){
                            toMainActivity(response.getData().getUname());
                        }else {
                            toLoginActivity();
                        }
                    }
                });
    }
    public void toMainActivity(String username){
        new Handler().postDelayed(() -> {
            MainActivity.startActivity(this,username);
            overridePendingTransition(R.anim.anim_launch_enter, 0);
            finish();
        }, 2000);
    }
    public void toLoginActivity(){
        new Handler().postDelayed(() -> {
            startActivity(new Intent(LeadActivity.this, LoginActivity.class));
            overridePendingTransition(R.anim.anim_launch_enter, 0);
            finish();
        }, 2000);
    }
}
