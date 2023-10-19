package com.example.schedulemanagement.view.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.app.Constants;
import com.example.schedulemanagement.utils.DateUtils;
import com.example.schedulemanagement.view.fragment.CalendarFragment;
import com.example.schedulemanagement.view.fragment.PersonFragment;
import com.example.schedulemanagement.view.fragment.TaskFragment;

import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private TextView mTextMessage;
    private ArrayList<Fragment> mFragments;
    private int mPreFragmentPosition;
    private FloatingActionButton addFloatingBtn;
    private String mDateText = "今天";
    private String mDateFormat;
    //选中的事件long类型
    private long mDateLong;
    private String mUsername;

    public static Context context;

    public static Activity activity;
    final int callbackId = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getApplicationContext();
        activity = this;
        setContentView(R.layout.activity_main);
        mUsername = getIntent().getStringExtra(Constants.KEY_NAME);
        checkPermission(callbackId, Manifest.permission.READ_CALENDAR, Manifest.permission.WRITE_CALENDAR);
        initView();
        initFragment();
    }

    //获取日历权限
    private void checkPermission(int callbackId, String... permissionsId) {
        boolean permissions = true;
        for (String p : permissionsId) {
            permissions = permissions && ContextCompat.checkSelfPermission(this, p) == PERMISSION_GRANTED;
        }

        if (!permissions)
            ActivityCompat.requestPermissions(this, permissionsId, callbackId);
    }

    @SuppressLint("RestrictedApi")
    private void initView() {
        mBottomNav = findViewById(R.id.navigation);
        mTextMessage = findViewById(R.id.message);
        addFloatingBtn = findViewById(R.id.addFloatingBtn);
        mDateFormat = DateUtils.getTodayDate();
        mDateLong = System.currentTimeMillis();
        mFragments = new ArrayList<>();

        mBottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    showAndHideFragment(0,mPreFragmentPosition);
                    mPreFragmentPosition = 0;
                    addFloatingBtn.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_today:
                    showAndHideFragment(1,mPreFragmentPosition);
                    mPreFragmentPosition = 1;
                    addFloatingBtn.setVisibility(View.VISIBLE);
                    return true;
                case R.id.navigation_person:
                    showAndHideFragment(2,mPreFragmentPosition);
                    mPreFragmentPosition = 2;
                    addFloatingBtn.setVisibility(View.GONE);
                    return true;
            }
            return false;
        });

        //添加日程
        addFloatingBtn.setOnClickListener(view -> AddActivity.startActivityByAdd(this,mDateText,mDateFormat,mDateLong));
    }

    private void initFragment() {
        mFragments.add(CalendarFragment.newInstance());
        mFragments.add(TaskFragment.newInstance());
        mFragments.add(PersonFragment.newInstance(mUsername));
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        for (int i = 0; i < mFragments.size(); i++) {
            transaction.add(R.id.frameContain,mFragments.get(i));
            if(i==0){
                transaction.show(mFragments.get(0));
            }else {
                transaction.hide(mFragments.get(i));
            }
        }
        transaction.commitAllowingStateLoss();
    }

    private void showAndHideFragment(int show, int hide) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (show != hide) {
            transaction.show(mFragments.get(show)).hide(mFragments.get(hide)).commitAllowingStateLoss();
        }
    }

    public void setDateText(String dateText,String dateFormat,long dateLong){
        mDateText = dateText;
        mDateFormat = dateFormat;
        mDateLong = dateLong;
    }

    /**
     * 供其它活动使用
     * @param activity
     * @param username
     */
    public static void startActivity(Activity activity,String username){
        Intent intent = new Intent(activity,MainActivity.class);
        intent.putExtra(Constants.KEY_NAME,username);
        activity.startActivity(intent);
    }

}
