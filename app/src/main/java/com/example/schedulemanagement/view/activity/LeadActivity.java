package com.example.schedulemanagement.view.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.base.entity.BaseResponse;
import com.example.schedulemanagement.callback.BaseResponseCallback;
import com.example.schedulemanagement.entity.LoginAndRegister;
import com.zhy.http.okhttp.OkHttpUtils;

public class LeadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lead);
        //ck();
        toMainActivity("Jay");
    }
    private void ck(){
        OkHttpUtils.post()
                .url(Constants.BASE_URL+"ck")
                .build()
                .execute(new BaseResponseCallback<LoginAndRegister>() {
                    @Override
                    public void onResponse(BaseResponse<LoginAndRegister> response, int id) {
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
