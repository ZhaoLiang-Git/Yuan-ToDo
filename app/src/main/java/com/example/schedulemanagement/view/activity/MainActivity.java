package com.example.schedulemanagement.view.activity;

import com.example.schedulemanagement.utils.DateUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.schedulemanagement.R;
import com.example.schedulemanagement.view.fragment.CalendarFragment;
import com.example.schedulemanagement.view.fragment.PersonFragment;
import com.example.schedulemanagement.view.fragment.TodayFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView mBottomNav;
    private TextView mTextMessage;
    private ArrayList<Fragment> mFragments;
    private int mPreFragmentPosition;
    private FloatingActionButton addFloatingBtn;
    private String mDateText = "今天";
    private String mDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initFragment();
    }

    private void initView() {
        mBottomNav = findViewById(R.id.navigation);
        mTextMessage = findViewById(R.id.message);
        addFloatingBtn = findViewById(R.id.addFloatingBtn);
        mDateFormat = DateUtils.getTodayDate();
        mFragments = new ArrayList<>();

        mBottomNav.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    showAndHideFragment(0,mPreFragmentPosition);
                    mPreFragmentPosition = 0;
                    return true;
                case R.id.navigation_today:
                    showAndHideFragment(1,mPreFragmentPosition);
                    mPreFragmentPosition = 1;
                    return true;
                case R.id.navigation_person:
                    showAndHideFragment(2,mPreFragmentPosition);
                    mPreFragmentPosition = 2;
                    return true;
            }
            return false;
        });

        //添加日程
        addFloatingBtn.setOnClickListener(view -> AddActivity.startActivity(this,mDateText,mDateFormat));
    }

    private void initFragment() {
        mFragments.add(CalendarFragment.newInstance());
        mFragments.add(TodayFragment.newInstance());
        mFragments.add(PersonFragment.newInstance());
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

    public void setDateText(String dateText,String dateFormat){
        mDateText = dateText;
        mDateFormat = dateFormat;
    }

}
